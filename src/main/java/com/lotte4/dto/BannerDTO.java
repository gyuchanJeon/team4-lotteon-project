package com.lotte4.dto;

import lombok.*;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerDTO implements Serializable {
    private static final long serialVersionUID = 1L; //2024.10.31 - 강중원 - 시리얼번호 추가
    private int bannerId;
    private String img;
    private String name;
    private String size;
    private String background;
    private String link;
    private String location;
    private Date sDate;
    private Date eDate;
    private String sTime;
    private String eTime;
    private int state;
}
