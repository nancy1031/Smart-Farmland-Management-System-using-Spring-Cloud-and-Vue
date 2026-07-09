package org.example.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartSearchResponse {
    private String analysis;          // AI分析结果
    private List<Product> products;   // 推荐商品
    private String recommendation;    // AI推荐语
    private String similarProducts;   // 相似商品推荐
}