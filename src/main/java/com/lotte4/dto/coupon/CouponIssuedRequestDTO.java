package com.lotte4.dto.coupon;

import com.lotte4.entity.User;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponIssuedRequestDTO {

    private int IssueId;
    private int couponId;
    private String uid;
    private int status;
    //사용일
    private LocalDateTime uDate;
}
