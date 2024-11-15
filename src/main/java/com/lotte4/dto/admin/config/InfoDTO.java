package com.lotte4.dto.admin.config;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;    //2024.10.31 - 강중원 - 시리얼번호 추가
    private int infoId;

    // 제목
    private String title;
    private String subTitle;

    // 로고
    private String headerLogo;
    private String favicon;

    // 기업 정보
    private String footerLogo;
    private String companyName;
    private String companyCeo;
    private String companyBusinessNumber;
    private String mosaNumber;
    private String companyAddress;
    private String companyAddress2;

    // 고객센터 정보
    private String csHp;
    private String csWorkingHours;
    private String csEmail;
    private String consumer;

    // 카피라이트
    private String copyright;

}
