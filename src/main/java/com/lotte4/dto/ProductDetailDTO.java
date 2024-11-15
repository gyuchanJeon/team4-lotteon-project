/*
     날짜 : 2024/10/30
     이름 : ???
     내용 : ProductDetailDTO 생성

     수정이력
     - 2024/10/30 전규찬 - productId 필드 추가
*/

package com.lotte4.dto;

import com.lotte4.entity.ProductDetail;
import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailDTO {
    //외래키 & 기본키
    private int productDetailId;

    private String condition_field;
    private String duty;
    private String receipt;
    private String sellerType;
    private String brand;
    private String coa;
    private String creator;
    private String country;
    private String warning;
    private String createDate;
    private String quality;

    private String as_field;
    private String asPhone;

    public ProductDetailDTO(ProductDetail productDetail) {
        this.productDetailId = productDetail.getProductDetailId();
        this.condition_field = productDetail.getCondition_field();
        this.duty = productDetail.getDuty();
        this.receipt = productDetail.getReceipt();
        this.sellerType = productDetail.getSellerType();
        this.brand = productDetail.getBrand();
        this.coa = productDetail.getCoa();
        this.creator = productDetail.getCreator();
        this.country = productDetail.getCountry();
        this.warning = productDetail.getWarning();
        this.createDate = productDetail.getCreateDate();
        this.quality = productDetail.getQuality();
        this.as_field = productDetail.getAs_field();
        this.asPhone = productDetail.getAsPhone();
    }
}
