/*
     날짜 : 2024/10/30
     이름 : ???
     내용 : ProductDTO 생성

     수정이력 : 2024-11-06 전규찬 평균평점 필드 추가, 생성일 필드 createdAt 추가

*/

package com.lotte4.dto;

import com.lotte4.entity.Product;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
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
    private SellerInfoDTO sellerInfoId;
    private LinkedHashMap<String, List<String>> options;
    private int status;
    private ProductCateDTO productCateId;
    private ProductDetailDTO productDetailId;
    private int rating;  // 2024-11-06 전규찬 평균평점 필드 추가
    private LocalDateTime createdAt;  // 2024-11-06 전규찬 생성일 필드 추가

    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.company = product.getCompany();
        this.price = product.getPrice();
        this.discount = product.getDiscount();
        this.point = product.getPoint();
        this.sold = product.getSold();
        this.deliveryFee = product.getDeliveryFee();
        this.hit = product.getHit();
        this.review = product.getReview();
        this.img1 = product.getImg1();
        this.img2 = product.getImg2();
        this.img3 = product.getImg3();
        this.detail = product.getDetail();
        this.status = product.getStatus();
        this.options = product.getOptions();
        this.rating = product.getRating();
        this.createdAt = product.getCreatedAt();

        // SellerInfoDTO 매핑
        if (product.getSellerInfoId() != null) {
            this.sellerInfoId = new SellerInfoDTO(product.getSellerInfoId());
        }

        // ProductCateDTO 매핑
        if (product.getProductCateId() != null) {
            this.productCateId = new ProductCateDTO(product.getProductCateId());
        }

        // ProductDetailDTO 매핑
        if (product.getProductDetail() != null) {
            this.productDetailId = new ProductDetailDTO(product.getProductDetail());
        }
    }
}

