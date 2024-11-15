/*
     날짜 : 2024/10/30
     이름 : ???
     내용 : ProductDetail 생성

     수정이력
     - 2024/10/30 전규찬 - productId 필드 추가
     - 2024/11/04 전규찬 - productId 필드 제거 / Product 측에 ProductDetailId 추가
*/

package com.lotte4.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "product_detail")
public class ProductDetail {
    //외래키 & 기본키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    //status 추가 11-05 조수빈
    private int status;

    // 2024/11/04 삭제 - 황수빈
    //    @OneToOne(mappedBy = "productDetail", fetch = FetchType.LAZY)
    //    private Product product;

}
