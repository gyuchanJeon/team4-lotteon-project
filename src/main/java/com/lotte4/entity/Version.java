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
@Table(name = "version")
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int versionId;

    // 유형
    private String versionName;
    // 내용
    private String content;
    // 작성한 시간
    @CreationTimestamp
    private LocalDateTime regDate;

    private String uid;
    // TODO : 나중에는 User로 변경
}
