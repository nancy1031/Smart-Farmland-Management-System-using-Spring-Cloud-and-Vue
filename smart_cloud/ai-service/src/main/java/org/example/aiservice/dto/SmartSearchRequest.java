package org.example.aiservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SmartSearchRequest {
    private String query;        // 用户搜索词
    private Long userId;         // 用户ID（可选）
    private String category;     // 商品分类（可选）
    private BigDecimal minPrice; // 最低价格（可选）
    private BigDecimal maxPrice; // 最高价格（可选）
    private Integer limit;       // 返回数量
}