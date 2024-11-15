package com.lotte4.dto;

import com.lotte4.entity.OrderItems;
import com.lotte4.entity.ProductVariants;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemsDTO {
    private int orderItemId;

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

    private int variantId;

    private ProductVariants variants;

    private ProductVariantsDTO productVariants;

    public OrderItemsDTO(OrderItems orderItems) {
        this.orderItemId = orderItems.getOrderItemId();
        this.count = orderItems.getCount();
        this.originPrice = orderItems.getOriginPrice();
        this.originDiscount = orderItems.getOriginDiscount();
        this.originPoint = orderItems.getOriginPoint();
        this.deliveryFee = orderItems.getDeliveryFee();
        this.status = orderItems.getStatus();
        this.variantId = orderItems.getVariantId();
    }
    private DeliveryDTO deliveryDTO;

}
