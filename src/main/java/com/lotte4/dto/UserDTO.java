package com.lotte4.dto;

import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.SellerInfo;
import com.lotte4.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private int userId;
    private MemberInfoDTO memberInfo;
    private SellerInfoDTO sellerInfo;

    private String uid;
    private String pass;
    private String role;

    @CreationTimestamp
    private String createdAt;
    private String leaveDate;

    // 사용자 ID 마스킹 처리 메서드
    public String getMaskedUid() {
        if (uid == null || uid.length() < 4) {
            return uid; // ID가 너무 짧은 경우 그대로 반환
        }
        // 아이디의 앞 2글자만 남기고, 나머지를 *로 마스킹
        String maskedUid = uid.substring(0, 2) + "*".repeat(uid.length() - 2);
        return maskedUid;
    }


}
