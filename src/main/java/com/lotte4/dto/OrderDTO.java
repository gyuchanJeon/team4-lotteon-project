package com.lotte4.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lotte4.entity.Order;
import com.lotte4.entity.Product;
import com.lotte4.entity.ProductVariants;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"productCate", "parent"})
public class OrderDTO {


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
    private int Status;
    private LocalDateTime buyDate;
    private int couponUse;
    private ProductVariantsDTO productVariants;
    private MemberInfoDTO memberInfo;
    private List<OrderItemsDTO> orderItems;


    // 배송메세지위한 추가(데이터 연결점이슈)
    private String content;

    public OrderDTO(Order order) {
        this.orderId = order.getOrderId();
        this.usePoint = order.getUsePoint();
        this.totalPrice = order.getTotalPrice();
        this.recipName = order.getRecipName();
        this.recipHp = order.getRecipHp();
        this.recipZip = order.getRecipZip();
        this.recipAddr1 = order.getRecipAddr1();
        this.recipAddr2 = order.getRecipAddr2();
        this.Payment = order.getPayment();
        this.Status = order.getStatus();
        this.buyDate = order.getBuyDate();
        this.couponUse = order.getCouponUse();

        // ProductVariantsDTO 매핑
        if (order.getProductVariants() != null) {
            this.productVariants = new ProductVariantsDTO(order.getProductVariants());
        }

        // MemberInfoDTO 매핑
        if (order.getMemberInfo() != null) {
            this.memberInfo = order.getMemberInfo().toDTO();
        }

        // List<OrderItemsDTO> 매핑
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            this.orderItems = order.getOrderItems().stream()
                    .map(OrderItemsDTO::new)
                    .collect(Collectors.toList());
        }
    }

    public OrderDTO(long l, Date date, String s, double v, long l1, String s1, String s2, long l2, int i, double v1, long l3, String s3, String s4) {
    }
}
