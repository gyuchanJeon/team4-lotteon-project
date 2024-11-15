package com.lotte4.entity;

import com.lotte4.dto.OrderDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "orders")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;
    private int usePoint;
    private int totalPrice;
    
    // 배송지
    private String recipName;
    private String recipHp;
    private String recipZip;
    private String recipAddr1;
    private String recipAddr2;

    //결재방법
    private int Payment;
    //order 빨리 끝나면 stauts 상태값 정리 할것 필수
    private int Status;
    //구매일자
    private LocalDateTime buyDate;
    //쿠폰 사용유무
    private int couponUse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberInfoId")
    private MemberInfo memberInfo;

    @OneToOne
    @JoinColumn(name = "couponId")
    private Coupon coupon;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariants productVariants;

    //null허용 안하면 지워야함 (error방지용)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrderItems> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItems orderItem) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderItems.add(orderItem);
        orderItem.setOrder(this); // 양방향 관계 설정
    }


}
