package com.lotte4.service;

import com.lotte4.dto.coupon.CouponIssuedRequestDTO;
import com.lotte4.entity.Coupon;
import com.lotte4.entity.CouponIssued;
import com.lotte4.entity.User;
import com.lotte4.repository.CouponIssuedRepository;
import com.lotte4.repository.admin.config.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CouponIssuedService {
    private final ModelMapper modelMapper;

    private final CouponRepository couponRepository;
    private final CouponIssuedRepository couponIssuedRepository;
    private final UserService userService;

    public boolean issueCoupon(CouponIssuedRequestDTO requestDTO) {
        // 이미 발급된 쿠폰인지 확인
        boolean alreadyIssued = couponIssuedRepository.existsByCoupon_couponIdAndUser_uid(requestDTO.getCouponId(), requestDTO.getUid());
        log.info("이미 존재한다 ?"+alreadyIssued);
        if (alreadyIssued) {
            return false;
        }

        // 쿠폰 발급 로직 수행 (CouponTake 객체 생성 후 저장)
        Coupon coupon = couponRepository.findById(requestDTO.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 쿠폰 ID입니다."));

        CouponIssued couponTake = CouponIssued.builder()
                .coupon(coupon)
                .user(modelMapper.map(userService.selectUser(requestDTO.getUid()), User.class))
                .build();

        couponIssuedRepository.save(couponTake);
        return true;
    }

}
