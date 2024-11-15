package com.lotte4.dto;

import lombok.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrderDetailDTO {
    private int orderId;
    private String recipName;
    private String recipHp;
    private String recipAddr;
    private int totalPrice;
    private int paymentMethod;
    private String orderDate;

    private List<OrderItemsDTO> orderItems;

    private String deliveryWaybill;
    private String deliveryCompany;
    private String deliveryDate;
}
