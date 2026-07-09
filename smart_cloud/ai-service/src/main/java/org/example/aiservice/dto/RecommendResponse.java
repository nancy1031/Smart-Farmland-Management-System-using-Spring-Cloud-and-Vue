package org.example.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendResponse {
    private String recommendation;  // AI推荐语
    private List<Product> products;  // 推荐商品列表
    private Integer totalCount;      // 总数
}