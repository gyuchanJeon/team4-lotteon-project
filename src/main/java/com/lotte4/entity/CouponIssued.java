package com.lotte4.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "coupon_Issued")
@Entity
public class CouponIssued {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int IssueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couponId")
    private Coupon coupon;

    //사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private int status;

    //사용일
   @CreationTimestamp
    private Date uDate;

}