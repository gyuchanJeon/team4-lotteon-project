package com.lotte4.controller.pagecontroller.admin.coupon;

import com.lotte4.dto.ProductDTO;
import com.lotte4.dto.coupon.CouponDTO;
import com.lotte4.dto.coupon.CouponRequestDTO;
import com.lotte4.entity.User;
import com.lotte4.security.MyUserDetails;
import com.lotte4.service.ProductService;
import com.lotte4.service.admin.config.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Controller
public class CouponController {

    private final CouponService couponService;
    private final ProductService productService;
    //쿠폰목록
    @GetMapping("/admin/coupon/list")
    public String AdminCouponList(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
        // 현재 로그인한 sellerId로 그 판매자의 상품을 뿌려줌

        if(Objects.equals(userDetails.getUser().getRole(), "seller")) {
            int sellerInfoId = userDetails.getUser().getSellerInfo().getSellerInfoId();
            List<ProductDTO> sellerProducts = productService.getAllProductBySellerId(sellerInfoId);
            model.addAttribute("sellerProducts", sellerProducts);
        }

        List<CouponDTO> couponDTOList = couponService.getAllCoupons();
        model.addAttribute("couponDTOList", couponDTOList);

        return "/admin/coupon/list";
    }

    @PostMapping("/admin/coupon/list")
    @ResponseBody
    public String AdminCouponInsert(@RequestBody CouponRequestDTO couponDTO, Model model) {
        log.info(couponDTO);
        try{
            couponService.insertCoupon(couponDTO);
            return "success";
        }
        catch (Exception e){
            log.error(e);
            return "fail";
        }
    }

    //쿠폰발급현황
    @GetMapping("/admin/coupon/issued")
    public String AdminCouponIssued() {
        return "/admin/coupon/issued";
    }
}
