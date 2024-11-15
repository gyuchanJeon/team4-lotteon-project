package com.lotte4.dto.mongodb;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserLogDTO {

    private String id;
    private String uid;
    private String eventType; // view, search, add_cart, delete_cart, order, review
    private int prodId;

    // 추가정보
    private String keyword; // search
    private int price;   // view, add_cart, delete_cart, order
    private int rating;  // review
    private int quantity; // add_cart, order

    private LocalDateTime timestamp; // 자동 생성 시간


}
