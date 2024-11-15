package com.lotte4.controller.pagecontroller.CSB;

import com.lotte4.dto.*;
import com.lotte4.entity.CouponIssued;
import com.lotte4.dto.coupon.CouponDTO;
import com.lotte4.entity.Order;
import com.lotte4.entity.Point;
import com.lotte4.entity.ProductVariants;
import com.lotte4.repository.OrderRepository;
import com.lotte4.repository.ProductVariantsRepository;
import com.lotte4.service.*;
import com.lotte4.service.admin.config.CouponService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Controller

public class OrderController {

    private final CartService cartService;
    private final ProductVariantsRepository productVariantsRepository;
    private final OrderService orderService;
    private final CouponService couponService;

    // 구매이전에는 세션이나 메모리에 잠시 보관하는것이 좋음.

    //장바구니
    @GetMapping("/product/order")
    public String CartBuyOrder(Model model, HttpSession session, Principal principal) {
        CartResponseDTO cartResponseDTO = (CartResponseDTO) session.getAttribute("directBuy"); // directBuy로 설정한 것만 조회
        List<CartItemDTO> cartItems = new ArrayList<>();

        // 로그인 체크
        if (principal == null) {
            return "redirect:/member/login";
        }

        String uid = principal.getName();

        UserPointCouponDTO point = orderService.selectUserPoint(uid);

        log.info("Point ==" + point);

        model.addAttribute("point", point);
        model.addAttribute("couponList", orderService.selectUserCoupon(uid));

        // 공통 메서드를 통한 CartItemDTO 생성
        if (cartResponseDTO != null) {  // 바로 구매 시
            List<Integer> ids = cartResponseDTO.getProductVariants();
            List<Integer> counts = cartResponseDTO.getCounts();

            // 상품 구매 정보 조회 //일단 되는것으로 빨리 끝내고 추후 시간 남으면 바로 처리
            List<ProductVariants> productVariantsList = productVariantsRepository.findByVariantIdIn(ids);
            log.info("variants" + productVariantsList);
            for (int i = 0; i < productVariantsList.size(); i++) {
                ProductVariants variant = productVariantsList.get(i);
                int count = counts.get(i);

                // 0으로 카트아이디는 설정
                // 공통화된 CartItemDTO 생성
                CartItemDTO item = CartItemDTO.createCartItemDTO(variant, count, 0);
                cartItems.add(item);
            }
        } else {  // 카트 구매 시
            List<Map<String, Object>> selectedCartItems = (List<Map<String, Object>>) session.getAttribute("selectedCartItems");
            if (selectedCartItems == null || selectedCartItems.isEmpty()) {
                return "redirect:/product/cart";
            }

            List<CartDTO> cartList = cartService.getCartItemsByIds(uid, selectedCartItems);
            for (CartDTO cart : cartList) {
                ProductVariants variant = cart.getProductVariants();

                Integer cartId = selectedCartItems.stream()
                        .filter(item -> item.get("cartId").equals(cart.getCartId()))
                        .map(item -> (Integer) item.get("cartId"))
                        .findFirst()
                        .orElse(0);

                CartItemDTO item = CartItemDTO.createCartItemDTO(variant, cart.getCount(), cartId);
                cartItems.add(item);
            }
            log.info("multiCart = {}", cartItems);
        }

        model.addAttribute("cartList", cartItems);
        session.setAttribute("selectedOrderItems", cartItems);
        return "/product/order";
    }


    @ResponseBody
    @PostMapping("/product/order")
    public ResponseEntity<String> CartBuyOrder(@RequestBody CartResponseDTO cartResponseDTO, HttpSession session) {
        if (cartResponseDTO != null) {
            session.setAttribute("directBuy", cartResponseDTO);
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.ok("fail");
        }
    }

    @GetMapping("/product/complete")
    public String complete(Model model, HttpSession session, Principal principal) {

        if (principal == null) {
            return "redirect:/member/login";
        }


        Object cartItemDTO = session.getAttribute("selectedOrderItems");
        if (cartItemDTO instanceof List<?>) {
            List<?> selectedOrderItems = (List<?>) cartItemDTO;

            if (selectedOrderItems.size() >= 2) {
                selectedOrderItems.stream()
                        .filter(item -> item instanceof CartItemDTO)
                        .map(item -> ((CartItemDTO) item).getCartId())
                        .forEach(cartService::deleteCartItems);
            }
        }
            List<Order> order = orderService.selectLastOrder();
            model.addAttribute("orderDTO", order);
            model.addAttribute("cartList", cartItemDTO);


            orderService.updateStock();


            return "/product/complete";

    }
}