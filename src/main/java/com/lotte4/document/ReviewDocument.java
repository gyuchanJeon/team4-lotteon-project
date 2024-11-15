package com.lotte4.document;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "reviews") // Mongdb의 Collection
public class ReviewDocument {

    @Id
    private String _id;

    private String uid;
    private int variantId;
    private int prodId;
    private int rating;
    private String content;
    private String regIp;
    private String img1;
    private String img2;

    @CreatedDate
    private LocalDateTime regDate;

}
