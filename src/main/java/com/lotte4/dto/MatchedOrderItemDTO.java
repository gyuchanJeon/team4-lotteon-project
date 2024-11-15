package com.lotte4.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lotte4.entity.OrderItems;
import com.lotte4.entity.ProductVariants;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchedOrderItemDTO {

    private OrderItems orderItem;
    private ProductVariants productVariant;

}

