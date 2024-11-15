package com.lotte4.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "point")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pointId;

    // 포인트 이름
    private String pointName;

    // 사용 혹은 적립 포인트 양
    private int point;
    // 잔여 포인트
    private int presentPoint;

    @CreationTimestamp
    private LocalDateTime pointDate;
    // 차감인지 사용인지
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberInfo_id")
    private MemberInfo memberInfo;

}
