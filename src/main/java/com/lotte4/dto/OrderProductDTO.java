package com.lotte4.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lotte4.entity.Order;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProductDTO {
    private Order order;
    private List<MatchedOrderItemDTO> matchedOrderItems;
}

