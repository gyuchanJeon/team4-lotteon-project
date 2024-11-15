package com.lotte4.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "info")
public class Info {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
