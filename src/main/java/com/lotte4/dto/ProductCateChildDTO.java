package com.lotte4.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lotte4.entity.ProductCate;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
    2024/10/31 강중원 - 자식데이터만 가지는 DTO추가
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ProductCateChildDTO implements Serializable {
    private static final long serialVersionUID = 1L;


    private int productCateId;

    // 이름
    private String name;

    // 계층
    private int depth; // 추가함(241024 10:25)

    // 인덱스
    private int cateIndex; // 추가 강중원 2024.11.07

    private List<ProductCateChildDTO> children = new ArrayList<>();

    public ProductCateChildDTO(ProductCate productCate) {
        this.productCateId = productCate.getProductCateId();
        this.name = productCate.getName();
        setChildren(productCate.getChildren());
    }

    public ProductCateChildDTO(int productCateId, String name) {
        this.productCateId = productCateId;
        this.name = name;
    }

    private void setChildren(List<ProductCate> children) {
        children.forEach(productCate -> {
            ProductCateChildDTO childDTO = new ProductCateChildDTO(productCate);
            this.children.add(childDTO);
        });
    }
}
