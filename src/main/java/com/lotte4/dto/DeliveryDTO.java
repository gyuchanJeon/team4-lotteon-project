package com.lotte4.dto;

import com.lotte4.entity.Order;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDTO {

    private Long deliveryId;
    //배송 시작일
    private LocalDateTime deliveryDate;
    //배송 무조건 완료일
    private LocalDateTime deliveryTime;
    //배송기업
    private String deliveryCompany;
    //송장번호
    private String deliveryWaybill;
    //송장생성일자
    private LocalDateTime waybillDate;
    private OrderDTO orderDTO1;
    private OrderItemsDTO orderDTO;

    private String content;

    public void setOrderDTO(int orderId) {

    }
    public void setOrderDTO(OrderDTO orderDTO) {
        this.orderDTO1 = orderDTO;
    }
}
