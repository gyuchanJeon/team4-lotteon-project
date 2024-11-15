package com.lotte4.entity;

import com.lotte4.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
/*
     날짜 : 2024/10/30
     이름 : 강은경
     내용 : User entity 생성

     수정이력
      - 2024/10/30 강은경 - toDTO 하는 과정에서 sellerInfo가 null일 경우 처리
*/
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    // 아이디
    private String uid;
    // 비밀번호
    private String pass;
    // 역할
    private String role;
    //
    @CreationTimestamp
    private String createdAt;
    private String leaveDate;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_info_id")
    private MemberInfo memberInfo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_info_id")
    private SellerInfo sellerInfo;


    public UserDTO toDTO() {
        return UserDTO.builder()
                .userId(userId)
                .memberInfo(memberInfo==null?null:memberInfo.toDTO())
                .sellerInfo(sellerInfo==null?null:sellerInfo.toDTO())
                .uid(uid)
                .pass(pass)
                .role(role)
                .createdAt(createdAt)
                .leaveDate(leaveDate)
                .build();
    }

}