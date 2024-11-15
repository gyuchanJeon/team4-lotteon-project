package com.lotte4.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_Items")
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;

    //아이템 옵션
    private String itemOption;

    //구매 수량
    private int count;

    //구매 당시 가격
    private int originPrice;

    //구매 당시 할인율
    private int originDiscount;

    //구매 당시 지급 포인트량
    private int originPoint;

    //배송비용
    private int deliveryFee;

    //주문당 상태값
    private int status;

    
    // 연결 값 없이 아이디값을 조회 할 수 있도록 할려고 함
    private int variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    @ToString.Exclude
    private Order order;


    @OneToOne(mappedBy = "orderItem")
    private Delivery delivery;


}
