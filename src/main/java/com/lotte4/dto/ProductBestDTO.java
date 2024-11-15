/*
     날짜 : 2024/10/30
     이름 : ???
     내용 : ProductDTO 생성

     수정이력
*/

package com.lotte4.dto;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductBestDTO {
    private int productId;
    private String name;
    private int price;
    private int discount;
    private String img1;
}

