package com.lotte4.dto.coupon;

import com.lotte4.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponRequestDTO {
    private String type;
    private String name;

    //benefit
    private String benefit;
    private int prodId;
    private Date sDate;
    private Date eDate;
    private int dDate;

    private int status;

    private int totalIssued;

    private int totalUsed;

    private String IDate;

    private String ect;

    //외래키
    private String uid;


}
