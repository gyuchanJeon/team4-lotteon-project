package com.lotte4.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "banner")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bannerId;
    //이미지 주소
    private String img;
    //배너 이름
    private String name;
    //배너 사이즈
    private String size;
    //배너 배경색
    private String background;
    //배너 다이렉트 링크
    private String link;
    //배너 위치
    private String location;
    //시작 날짜
    private Date sDate;
    //끝 날짜
    private Date eDate;
    //시작 시간
    private String sTime;
    //끝 시간
    private String eTime;
    //상태
    private int state;
}
