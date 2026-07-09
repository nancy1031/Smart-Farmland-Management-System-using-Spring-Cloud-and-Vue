package org.example.aiservice.client;

import org.example.aiservice.dto.Product;
import java.util.List;
import java.util.Collections;

public class ProductClientFallback implements ProductClient {
    @Override
    public Product getProductById(Long id) {
        Product fallback = new Product();
        fallback.setName("商品服务暂时不可用");
        fallback.setDescription("请稍后再试");
        return fallback;
    }
    @Override
    public List<Product> searchProducts(String keyword) {
        return Collections.emptyList();
    }
    @Override
    public List<Product> getRecommendProducts(Long userId) {
        return Collections.emptyList();
    }
    @Override
    public List<Product> getHotProducts(Integer limit) {
        return Collections.emptyList();
    }
    @Override
    public List<Product> getProductsByCategory(String category) {
        return Collections.emptyList();
    }
}