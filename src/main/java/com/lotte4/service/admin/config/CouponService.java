package com.lotte4.service.admin.config;

import com.lotte4.dto.ProductDTO;
import com.lotte4.dto.SellerInfoDTO;
import com.lotte4.dto.coupon.CouponDTO;
import com.lotte4.dto.UserDTO;
import com.lotte4.dto.coupon.CouponIssuedResponseDTO;
import com.lotte4.dto.coupon.CouponRequestDTO;
import com.lotte4.dto.coupon.CouponResponseDTO;
import com.lotte4.entity.Coupon;
import com.lotte4.entity.CouponIssued;
import com.lotte4.entity.Product;
import com.lotte4.entity.User;
import com.lotte4.repository.CouponIssuedRepository;
import com.lotte4.repository.admin.config.CouponRepository;
import com.lotte4.service.CouponIssuedService;
import com.lotte4.service.ProductService;
import com.lotte4.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page; // 2024/11/13 황수빈
import org.springframework.data.domain.Pageable; // 2024/11/13 황수빈
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CouponService {

    private final ModelMapper modelMapper;
    private final CouponRepository couponRepository;
    private final UserService userService;
    private final ProductService productService;
    private final CouponIssuedService couponIssuedService; // 2024/11/11 황수빈 추가
    private final CouponIssuedRepository couponIssuedRepository;

    public List<CouponDTO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        List<CouponDTO> couponDTOS = new ArrayList<>();

        for(Coupon coupon : coupons){
            CouponDTO couponDTO = modelMapper.map(coupon, CouponDTO.class);
            couponDTO.setIDate(couponDTO.getIDate().substring(0,10));
            couponDTO = changeBenefit(coupon, couponDTO);
            couponDTOS.add(couponDTO);
        }
        return couponDTOS;
    }

    public void insertCoupon(CouponRequestDTO couponDTO) {

        UserDTO user = userService.selectUser(couponDTO.getUid());
        Coupon coupon = modelMapper.map(couponDTO, Coupon.class);
        coupon.setUsers(modelMapper.map(user, User.class));

        log.info(coupon);
        couponRepository.save(coupon);
    }

    public CouponDTO changeBenefit(Coupon coupon, CouponDTO couponDTO) {
        int benefit = coupon.getBenefit();

        switch (benefit) {
            case 1:
                couponDTO.setBenefit("1,000원 할인");
                break;
            case 2:
                couponDTO.setBenefit("2,000원 할인");
                break;
            case 3:
                couponDTO.setBenefit("3,000원 할인");
                break;
            case 4:
                couponDTO.setBenefit("4,000원 할인");
                break;
            case 5:
                couponDTO.setBenefit("5,000원 할인");
                break;
            case 6:
                couponDTO.setBenefit("10% 할인");
                break;
            case 7:
                couponDTO.setBenefit("20% 할인");
                break;
            case 8:
                couponDTO.setBenefit("30% 할인");
                break;
            case 9:
                couponDTO.setBenefit("40% 할인");
                break;
            case 10:
                couponDTO.setBenefit("50% 할인");
                break;
            case 0:
                couponDTO.setBenefit("배송비 무료");
                break;
        }

        return couponDTO;
    }
    public List<CouponDTO> getAvailableCouponsForProduct(int productId) {

        ProductDTO product = productService.getProductById(productId);
        SellerInfoDTO sellerInfoId = product.getSellerInfoId();

        List<Coupon> coupons = couponRepository.findAllByConditions(sellerInfoId.getSellerInfoId(), productId);

        return coupons.stream()
                .map(coupon -> modelMapper.map(coupon, CouponDTO.class))
                .collect(Collectors.toList());
    }

    public Page<CouponIssuedResponseDTO> getIssuedCouponsByUid(String uid, Pageable pageable) {
        Page<CouponIssued> issuedCoupons = couponIssuedRepository.findByUser_uid(uid, pageable);

        return issuedCoupons.map(coupon -> modelMapper.map(coupon, CouponIssuedResponseDTO.class));
    } // 2024/11/13 황수빈
}
