package com.lotte4.dto;

import com.lotte4.entity.Cart;
import com.lotte4.entity.ProductVariants;
import com.lotte4.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {

    private int cartId;

    private UserDTO user;

    private ProductVariants productVariants;

    // 갯수
    private int count;

    // 날짜
    @CreationTimestamp
    private String rDate;



}
