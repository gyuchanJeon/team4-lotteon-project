/*
     날짜 : 2024/10/30
     이름 : ???
     내용 : ProductDTO 생성

     수정이력
*/

package com.lotte4.dto;

import lombok.*;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int productId;
    private String name;
    private String description;
    private int price;
    private int discount;
    private int point;
    private int sold;
    private int deliveryFee;
    private int hit;
    private int review;
    private String img1;
    private int status;
    private String createdAt;
    private int rating;
}

