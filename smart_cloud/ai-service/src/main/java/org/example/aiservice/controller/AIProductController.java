package org.example.aiservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.aiservice.dto.RecommendResponse;
import org.example.aiservice.dto.SmartSearchRequest;
import org.example.aiservice.dto.SmartSearchResponse;
import org.example.aiservice.service.ProductAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai/product")
public class AIProductController {

    @Autowired
    private ProductAIService productAIService;

    @PostMapping("/smart-search")
    public Map<String, Object> smartSearch(@RequestBody SmartSearchRequest request) {
        log.info("收到智能搜索请求：query={}, userId={}", request.getQuery(), request.getUserId());
        SmartSearchResponse response = productAIService.smartSearch(request);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", response);
        return result;
    }

    @GetMapping("/qa")
    public Map<String, Object> productQA(
            @RequestParam Long productId,
            @RequestParam String question) {
        log.info("收到商品问答请求：productId={}, question={}", productId, question);
        String answer = productAIService.answerProductQuestion(productId, question);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", Map.of(
                "productId", productId,
                "question", question,
                "answer", answer
        ));
        return result;
    }

    @GetMapping("/recommend/{userId}")
    public Map<String, Object> recommendProducts(@PathVariable Long userId) {
        log.info("收到商品推荐请求：userId={}", userId);
        String recommendation = productAIService.recommendProducts(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", recommendation);
        return result;
    }

    @GetMapping("/recommend/detail/{userId}")
    public Map<String, Object> getDetailedRecommendations(@PathVariable Long userId) {
        log.info("收到详细商品推荐请求：userId={}", userId);
        RecommendResponse response = productAIService.getDetailedRecommendations(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", response);
        return result;
    }
}