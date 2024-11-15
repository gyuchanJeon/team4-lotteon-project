package com.lotte4.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lotte4.entity.ProductCate;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRegisterCateDTO {
    private int productCateId;

    // 이름
    private String name;

    // 계층
    private int depth;

}
