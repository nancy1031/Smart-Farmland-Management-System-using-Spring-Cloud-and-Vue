package org.example.aiservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.aiservice.client.ProductClient;
import org.example.aiservice.dto.Product;
import org.example.aiservice.dto.RecommendResponse;
import org.example.aiservice.dto.SmartSearchRequest;
import org.example.aiservice.dto.SmartSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductAIService {

    @Autowired
    private ProductClient productClient;

    @Autowired
    private AIService aiService;

    // 删掉这行，解决自循环依赖
    // @Autowired
    // private ProductAIService self;

    public SmartSearchResponse smartSearch(SmartSearchRequest request) {
        SmartSearchResponse response = new SmartSearchResponse();
        try {
            String keyword = request.getQuery();
            List<Product> products = productClient.searchProducts(keyword);
            if (products.isEmpty()) {
                products = productClient.getHotProducts(5);
                response.setAnalysis("未找到完全匹配的商品，为您推荐热销商品");
            } else {
                response.setAnalysis("为您找到以下相关商品");
            }
            if (request.getMinPrice() != null) {
                products = products.stream()
                        .filter(p -> p.getPrice().compareTo(request.getMinPrice()) >= 0)
                        .collect(Collectors.toList());
            }
            if (request.getMaxPrice() != null) {
                products = products.stream()
                        .filter(p -> p.getPrice().compareTo(request.getMaxPrice()) <= 0)
                        .collect(Collectors.toList());
            }
            int limit = request.getLimit() != null ? request.getLimit() : 10;
            if (products.size() > limit) {
                products = products.subList(0, limit);
            }
            String recommendation = aiService.generateRecommendation(products);
            response.setProducts(products);
            response.setRecommendation(recommendation);
            String analysis = analyzeSearch(request.getQuery(), products);
            response.setAnalysis(analysis);
        } catch (Exception e) {
            log.error("智能搜索失败", e);
            response.setAnalysis("搜索服务暂时不可用，请稍后再试");
            response.setProducts(new ArrayList<>());
        }
        return response;
    }

    // 直接调用本类缓存方法，不再使用self
    public String answerProductQuestion(Long productId, String question) {
        return this.answerProductQuestionWithCache(productId, question);
    }

    @Cacheable(value = "productQA", key = "#productId + '_' + #question")
    public String answerProductQuestionWithCache(Long productId, String question) {
        try {
            Product product = productClient.getProductById(productId);
            if (product == null) {
                return "抱歉，未找到该商品信息";
            }
            String prompt = String.format(
                    "商品信息：\n" +
                            "名称：%s\n" +
                            "品牌：%s\n" +
                            "分类：%s\n" +
                            "价格：%s元\n" +
                            "描述：%s\n" +
                            "规格：%s\n" +
                            "评分：%s\n" +
                            "销量：%s\n" +
                            "\n用户问题：%s\n" +
                            "\n请根据以上商品信息，用专业、友好的语气回答用户的问题。",
                    product.getName(),
                    product.getBrand(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getSpecs() != null ? product.getSpecs() : "暂无规格信息",
                    product.getRating() != null ? product.getRating() : "暂无评分",
                    product.getSalesCount() != null ? product.getSalesCount() : "暂无销量数据",
                    question
            );
            return aiService.chat(prompt);
        } catch (Exception e) {
            log.error("商品问答失败", e);
            return "AI服务暂时不可用，请稍后再试";
        }
    }

    public String recommendProducts(Long userId) {
        try {
            log.info("开始为用户 {} 生成智能推荐", userId);
            List<Product> products = productClient.getRecommendProducts(userId);
            if (products == null || products.isEmpty()) {
                log.info("用户 {} 没有推荐商品，获取热销商品", userId);
                products = productClient.getHotProducts(5);
                if (products.isEmpty()) {
                    return "暂无推荐商品，请稍后再试";
                }
                return aiService.generateRecommendation(products) + "\n\n💡 这些是当前热销商品，供您参考。";
            }
            if (products.size() > 5) {
                products = products.subList(0, 5);
            }
            String recommendation = aiService.generateRecommendation(products);
            return String.format(
                    "🎯 基于您的购物偏好，为您推荐以下商品：\n\n%s\n\n📌 推荐商品数量：%d个",
                    recommendation,
                    products.size()
            );
        } catch (Exception e) {
            log.error("商品推荐失败，userId: {}", userId, e);
            return "推荐服务暂时不可用，请稍后再试。";
        }
    }

    public List<Product> getRecommendProductsList(Long userId) {
        try {
            List<Product> products = productClient.getRecommendProducts(userId);
            if (products == null || products.isEmpty()) {
                products = productClient.getHotProducts(5);
            }
            if (products.size() > 5) {
                products = products.subList(0, 5);
            }
            return products;
        } catch (Exception e) {
            log.error("获取推荐商品失败", e);
            return new ArrayList<>();
        }
    }

    public String generateRecommendation(List<Product> products) {
        return aiService.generateRecommendation(products);
    }

    public RecommendResponse getDetailedRecommendations(Long userId) {
        try {
            log.info("开始获取用户 {} 的详细推荐", userId);
            List<Product> products = getRecommendProductsList(userId);
            if (products == null || products.isEmpty()) {
                return new RecommendResponse("暂无推荐商品", new ArrayList<>(), 0);
            }
            String recommendation = generateRecommendation(products);
            return new RecommendResponse(recommendation, products, products.size());
        } catch (Exception e) {
            log.error("获取详细推荐失败", e);
            return new RecommendResponse("推荐服务暂时不可用", new ArrayList<>(), 0);
        }
    }

    private String analyzeSearch(String query, List<Product> products) {
        if (products.isEmpty()) {
            return "抱歉，没有找到相关商品。";
        }
        try {
            String prompt = String.format(
                    "用户搜索词：%s\n" +
                            "搜索结果数量：%d个\n" +
                            "商品名称列表：%s\n" +
                            "请分析用户的购物意图，用一句话总结。",
                    query,
                    products.size(),
                    products.stream()
                            .map(Product::getName)
                            .collect(Collectors.joining("、"))
            );
            return aiService.chat(prompt);
        } catch (Exception e) {
            log.error("搜索分析失败", e);
            return "找到 " + products.size() + " 个相关商品";
        }
    }
}