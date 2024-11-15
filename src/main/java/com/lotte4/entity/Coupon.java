package com.lotte4.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "coupon")
@Entity
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int couponId;
    //쿠폰종류
    private String type;
    //쿠폰 이름
    private String name;
    private int prodId;
    private int benefit;


    private Date sDate;
    private Date eDate;

    // duration_days 예) 50일
    private int dDate;
    private int status;

    //발급자 확인용 User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User users;

    // 발급수

    private int totalIssued;

    //사용수
    private int totalUsed;

    // 발급일
    @CurrentTimestamp
    private LocalDateTime iDATE;

    //유의 사항
    private String ect;

    @OneToOne(mappedBy = "coupon")
    private Order order;


}


