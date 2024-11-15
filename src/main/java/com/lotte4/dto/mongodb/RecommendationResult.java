package com.lotte4.dto.mongodb;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationResult {
    private int relatedProdId;            // 연관 상품 ID
    private int viewCount;

}
