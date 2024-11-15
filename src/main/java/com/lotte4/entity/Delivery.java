package com.lotte4.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "delivery")
@Entity
public class Delivery {

    //배송 순번(추후 년/월/일 + 번호 같이 만들어서 줘야함)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliveryId")
    private Long deliveryId;


    //배송 시작일
    @CreationTimestamp
    private LocalDateTime deliveryDate = LocalDateTime.now();

    //배송 무조건 완료일
    private LocalDateTime deliveryTime = deliveryDate = deliveryDate.plusDays(3);

    //배송기업
    private String deliveryCompany;

    //송장번호
    private String deliveryWaybill;

    //송장생성일자
    @CreationTimestamp
    private LocalDateTime waybillDate;

    private String content;


    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "orderItemId")  // OrderItems의 orderItemId를 외래 키로 사용
    private OrderItems orderItem;

}

