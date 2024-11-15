package com.lotte4.dto;

import com.lotte4.entity.User;
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
public class PointDTO {

    private int pointId;

    // 포인트 이름
    private String pointName;

    // 사용 혹은 적립 포인트 양
    private int point;
    // 잔여 포인트
    private int presentPoint;


    private LocalDateTime pointDate;
    // 차감인지 사용인지
    private String type;

    private MemberInfoDTO memberInfo;

    // 추가 필드
    private String uid;
}
