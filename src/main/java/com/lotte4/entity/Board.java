package com.lotte4.entity;

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
@Entity
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardId;

    // 유형
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="boardCateId")
    private BoardCate cate;

    // 제목
    private String title;
    private String content;
    private String type;

    private int state;
    private String comment;
    private String regIp;
    @CreationTimestamp
    private LocalDateTime regDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="writer")
    private User user;

    @Transient
    private String maskedUserId;

}
