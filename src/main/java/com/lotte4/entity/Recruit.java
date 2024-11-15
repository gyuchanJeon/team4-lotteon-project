package com.lotte4.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "recruit")
public class Recruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recruitId;
    // 채용부서
    private String division;
    // 경력사항
    private String career;
    // 제목
    private String title;
    // 작성자
    private String author;
    // 채용 형태
    private String employment;
    // 채용 시작 날짜

    private LocalDate sDate;
    // 채용 종료 날짜
    private LocalDate eDate;
    // 작성날짜
    private LocalDateTime regdate;
    // 비고
    private String content;
    // 채용상태
    private String status;

}
