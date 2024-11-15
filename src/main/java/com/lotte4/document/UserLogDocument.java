package com.lotte4.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "userLogs") // Mongdb의 Collection
public class UserLogDocument {
    @Id
    private String id;

    private String uid;
    private String eventType; // view, search, add_cart, delete_cart, order, review
    private int prodId;

    // 추가정보
    private String keyword; // search
    private int price;   // view, add_cart, delete_cart, order
    private int rating;  // review
    private int quantity; // add_cart, order

    @CreatedDate
    private LocalDateTime timestamp; // 자동 생성 시간

}
