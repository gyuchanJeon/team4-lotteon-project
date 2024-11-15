package com.lotte4.document;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "IssuedCoupon") // Mongdb의 Collection
public class MemberCouponDocument {
    @Id
    private String _id;

    private int couponId; // 해당 Coupon의 Id
    private String type; // 해당 Coupon의 type
    private String name; // 해당 Coupon의 name
    private int benefit; // 해당 Coupon의 할인율

    private String uid;
    private String status; // 사용가능 | 만료 | 사용완료

    //사용일
    private Date uDate;

}
