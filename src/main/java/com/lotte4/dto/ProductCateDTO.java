package com.lotte4.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lotte4.entity.ProductCate;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/*

    -2024-10-28 강중원 - tostring 순환참조 제거

 */


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ProductCateDTO {
    private int productCateId;

    // 이름
    private String name;

    // 계층
    private int depth; // 추가함(24/10/24 10:25)

    // 부모
    @ToString.Exclude
    @JsonBackReference // 추가 - 황수빈 (24/10/30 12:27)
    private ProductCateDTO parent; // 추가함(24/10/24 12:35)
    @JsonManagedReference
    private List<ProductCateDTO> children = new ArrayList<>();

    public ProductCateDTO(ProductCate productCate) {
        this.productCateId = productCate.getProductCateId();
        this.name = productCate.getName();
        setChildren(productCate.getChildren());
    }

    public ProductCateDTO(int productCateId, String name) {
        this.productCateId = productCateId;
        this.name = name;
    }

    private void setChildren(List<ProductCate> children) {
        children.forEach(productCate -> {
            ProductCateDTO childDTO = new ProductCateDTO(productCate);
            this.children.add(childDTO);
        });
    }
}
