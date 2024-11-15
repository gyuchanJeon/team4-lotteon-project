package com.lotte4.dto;

import com.lotte4.entity.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderWithDetailsDTO {

    private Order order;
    private OrderItems orderItem;
    private MemberInfo memberInfo;
    private ProductVariants productVariant;
    private Product product;
    private User user;
    private Delivery delivery;
    private SellerInfo sellerInfo;

    public OrderWithDetailsDTO(Order order, OrderItems orderItem, MemberInfo memberInfo,
                               ProductVariants productVariant, Product product, Delivery delivery) {
        this.order = order;
        this.orderItem = orderItem;
        this.memberInfo = memberInfo;
        this.productVariant = productVariant;
        this.product = product;
        this.delivery = delivery;
    }

}
