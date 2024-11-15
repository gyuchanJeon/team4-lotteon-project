/*
     날짜 : 2024/11/06
     이름 : 전규찬
     내용 : product 엔티티 생성

     수정이력
     - 2024/11/06 전규찬 - 평균 평점을 위한 새로운 필드 rating 추가, 생성일 필드 createdAt 추가
*/

package com.lotte4.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lotte4.config.MapStringListToJsonConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    private String name;
    private String description;
    private String company;
    private int price;
    private int discount;
    private int point;
    private int sold;
    private int deliveryFee;
    private int hit;
    private int review;
    private String img1;
    private String img2;
    private String img3;
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellerInfoId")
    private SellerInfo sellerInfoId;

    @Builder.Default
    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private List<ProductVariants> productVariants = new ArrayList<>();

    @Convert(converter = MapStringListToJsonConverter.class)
    private LinkedHashMap<String, List<String>> options;

    private int status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productCateId")
    private ProductCate productCateId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productDetailId")
    private ProductDetail productDetail;
    
    private int rating;  // 2024-11-06 전규찬 평균평점 필드 추가

    @CurrentTimestamp
    private LocalDateTime createdAt;  // 2024-11-06 전규찬 생성일 필드 추가

    // 관계 관리 메서드
    public void addVariant(ProductVariants variant) {
        productVariants.add(variant);
        variant.setProduct(this);
    }

    public void removeVariant(ProductVariants variant) {
        productVariants.remove(variant);
        variant.setProduct(null);
    }
}

