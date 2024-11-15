package com.lotte4.entity;

import com.lotte4.dto.AddressDTO;
import com.lotte4.dto.MemberInfoDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "member_info")
@Entity
public class MemberInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int memberInfoId;
    private String name;
    private int gender;
    private String email;
    private String hp;

    @Embedded
    private Address address; // address 객체로 분리

    private int point;

    @CreationTimestamp
    private String updatedAt;
    private String lastLoginAt; // 최근 로그인 날짜

    private String status;
    private String grade;
    private String etc;

//    @Enumerated(EnumType.ORDINAL)
//    private status status;
//    @Enumerated(EnumType.ORDINAL)
//    private grade grade;

    public enum status{
        정상, // 0번
        중지, // 1번
        휴면, // 2번
        탈퇴  // 3번
    }

    public enum grade{
        ADMIN,  //0번
        FAMILY, //1번
        SILVER, //2번
        GOLD,   //3번
        VIP,    //4번
        VVIP,   //5번
        SELLER, //6번
    }

    // status와 grade에 디폴트값이 안들어가서 추가
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "정상";
        }
        if (this.grade == null) {
            this.grade = "FAMILY";
        }
    }

    public MemberInfoDTO toDTO() {
        return MemberInfoDTO.builder()
                .memberInfoId(this.memberInfoId)
                .name(this.name)
                .gender(this.gender)
                .email(this.email)
                .hp(this.hp)
                .address(new AddressDTO(address.getZipCode(), address.getAddr1(), address.getAddr2()))  // Address 포함
                .point(this.point)
                .updatedAt(this.updatedAt)
                .lastLoginAt(this.lastLoginAt)
                .status(this.status)
                .grade(this.grade)
                .etc(this.etc)
                .build();
    }


}