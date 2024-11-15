package com.lotte4.dto.coupon;

import com.lotte4.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponIssuedResponseDTO {

    private int IssueId;

    private CouponDTO coupon;
    private User user;
    private int status;
    //사용일
    private Date uDate;
}
