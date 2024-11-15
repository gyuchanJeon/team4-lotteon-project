package com.lotte4.repository.admin.config;

import com.lotte4.dto.coupon.CouponDTO;
import com.lotte4.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    @Query("SELECT c FROM Coupon c WHERE c.users.memberInfo.memberInfoId = :memberInfoId")
    List<CouponDTO> findCouponsByMemberInfoId(@Param("memberInfoId") int memberInfoId);
    @Query("SELECT c FROM Coupon c WHERE (c.users.sellerInfo.sellerInfoId = :sellerInfoId OR c.users.role = 'Admin' OR c.prodId = :productId) AND c.status = 1")
    List<Coupon> findAllByConditions(@Param("sellerInfoId") int sellerInfoId, @Param("productId") int productId);
    }

