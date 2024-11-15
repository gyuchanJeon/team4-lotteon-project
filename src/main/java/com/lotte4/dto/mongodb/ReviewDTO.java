package com.lotte4.dto.mongodb;

import com.lotte4.entity.ProductVariants;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {

    private String uid;
    private int variantId;
    private int prodId;
    private int rating;
    private String content;
    private String regIp;
    private String img1;
    private String img2;
    private LocalDateTime regDate;

    // 추가 필드
    private ProductVariants productVariants;



}
