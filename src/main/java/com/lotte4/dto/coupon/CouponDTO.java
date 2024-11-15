package com.lotte4.dto.coupon;

import com.lotte4.dto.UserDTO;
import lombok.*;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDTO {
    private int couponId;
    private String type;
    private String name;
    private int prodId;
    //benefit
    private String benefit;

    private Date sDate;
    private Date eDate;
    private int dDate;

    private int status;
    private int totalIssued;
    private int totalUsed;
    private String iDate;
    private String ect;

    //외래키
    private UserDTO users;
    private int ProductId;

}
