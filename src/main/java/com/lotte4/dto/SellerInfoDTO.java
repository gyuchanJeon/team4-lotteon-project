package com.lotte4.dto;

import com.lotte4.entity.Address;
import com.lotte4.entity.SellerInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
/*
     날짜 : 2024/11/03
     이름 : 강은경
     내용 : SellerInfoDTO 생성

     수정이력
      - 2024/11/03 강은경 - email 속성 추가
      - 2024/11/07 전규찬 - grade 속성 추가
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerInfoDTO {

    private int sellerInfoId;
    // 회사 이름
    private String comName;
    // 대표자 명
    private String ceo;
    // 사업자 등록번호
    private String comNumber;
    // 통신판매업 번호
    private String bizNumber;
    // 전화 번호
    private String hp;
    // 팩스 번호
    private String fax;
    private String email;

    private AddressDTO address;

    // 아이피
    private String regIp;

    @CreationTimestamp
    // 변경 일자
    private String updateAt;
    // 상태
    private int state;
    // 등급
    private String grade;

    public SellerInfoDTO(SellerInfo sellerInfo) {
        if (sellerInfo != null) {
            this.sellerInfoId = sellerInfo.getSellerInfoId();
            this.comName = sellerInfo.getComName();
            this.ceo = sellerInfo.getCeo();
            this.comNumber = sellerInfo.getComNumber();
            this.bizNumber = sellerInfo.getBizNumber();
            this.hp = sellerInfo.getHp();
            this.fax = sellerInfo.getFax();
            this.regIp = sellerInfo.getRegIp();
            this.updateAt = sellerInfo.getUpdateAt() != null ? sellerInfo.getUpdateAt().toString() : null;
            this.state = sellerInfo.getState();
            this.grade = sellerInfo.getGrade();

            // Address 매핑
            if (sellerInfo.getAddress() != null) {
                this.address = new AddressDTO(sellerInfo.getAddress());
            }
        }
    }

    public SellerInfo toEntity() {
        return SellerInfo.builder()
                .sellerInfoId(sellerInfoId)
                .comName(comName)
                .ceo(ceo)
                .comNumber(comNumber)
                .bizNumber(bizNumber)
                .hp(hp)
                .fax(fax)
                .email(email)
                .address(new Address(address.getZipCode(), address.getAddr1(), address.getAddr2()))
                .regIp(regIp)
                .updateAt(updateAt)
                .state(state)
                .grade(grade)
                .build();
    }

}
