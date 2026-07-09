package org.example.aiservice.client;

import org.example.aiservice.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// 关键：fallback = ProductClientFallback.class
@FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable("id") Long id);

    @GetMapping("/api/products/search")
    List<Product> searchProducts(@RequestParam("keyword") String keyword);

    @GetMapping("/api/products/recommend")
    List<Product> getRecommendProducts(@RequestParam("userId") Long userId);

    @GetMapping("/api/products/hot")
    List<Product> getHotProducts(@RequestParam("limit") Integer limit);

    @GetMapping("/api/products/category/{category}")
    List<Product> getProductsByCategory(@PathVariable("category") String category);
}