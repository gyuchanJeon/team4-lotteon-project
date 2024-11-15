package com.lotte4.dto;

import com.lotte4.dto.coupon.CouponDTO;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPointCouponDTO {

    private int totalPoints;
    private int couponId;
    private CouponDTO coupon;
    private PointDTO point;
    private UserDTO user;
    private int status;
    private OrderItemsDTO orderItems;
    private OrderDTO order;

    private int previousTotalPoints;
    private CouponDTO previousCoupon;
    private PointDTO previousPoint;
    private OrderItemsDTO previousOrderItems;
    private OrderDTO previousOrder;



    private void saveCurrentState() {
        this.previousTotalPoints = this.totalPoints;
        this.previousCoupon = this.coupon;
        this.previousPoint = this.point;
        this.previousOrderItems = this.orderItems;
        this.previousOrder = this.order;
    }



    public void userUsePoint(PointDTO point) {
        saveCurrentState();
        this.point = point;
        if (orderItems.getStatus() == 1){
            totalPoints = point.getPoint() + orderItems.getOriginPoint();
            point.setPoint(totalPoints);
        }
    }

    public UserPointCouponDTO(Integer totalPoints) {
        this.totalPoints = (totalPoints != null) ? totalPoints.intValue() : 0;
    }

    public void userUseCoupon(CouponDTO coupon) {
        saveCurrentState();
        this.coupon = coupon;
        if (orderItems.getStatus() == 1){
            if(order.getCouponUse() == 1){
                coupon.setStatus(1);
            }
        }
    }

    //이전 상태로 되돌리는 메서드
    public void Return() {
        this.totalPoints = this.previousTotalPoints;
        this.coupon = this.previousCoupon;
        this.point = this.previousPoint;
        this.orderItems = this.previousOrderItems;
        this.order = this.previousOrder;
    }

}
