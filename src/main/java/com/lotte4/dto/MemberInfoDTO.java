package com.lotte4.dto;

import com.lotte4.entity.Address;
import com.lotte4.entity.MemberInfo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Member;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoDTO {

    private int memberInfoId;
    private String name;
    private int gender;
    private String email;
    private String hp;
    private AddressDTO address;
    private int point;
    private String updatedAt;
    private String lastLoginAt;
    private String status;
    private String grade;
    private String etc;


    // 이름 마스킹 처리 메서드
    public String getMaskedName() {
        if (name == null || name.length() < 2) {
            return name; // 이름이 너무 짧은 경우 그대로 반환
        }
        // 이름의 첫 글자와 마지막 글자는 그대로 두고, 가운데 글자를 *로 마스킹
        String maskedName = name.substring(0, 1) + "*".repeat(name.length() - 2) + name.substring(name.length() - 1);
        return maskedName;
    }

    public MemberInfo toEntity() {
        return MemberInfo.builder()
                .memberInfoId(this.memberInfoId)
                .name(this.name)
                .gender(this.gender)
                .email(this.email)
                .hp(this.hp)
                .address(new Address(address.getZipCode(), address.getAddr1(), address.getAddr2()))
                .point(this.point)
                .updatedAt(this.updatedAt)
                .lastLoginAt(this.lastLoginAt)
                .status(this.status)
                .grade(this.grade)
                .etc(this.etc)
                .build();
    }

}
