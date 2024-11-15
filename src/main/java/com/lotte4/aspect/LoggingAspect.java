package com.lotte4.aspect;

import com.lotte4.dto.CartResponseDTO;
import com.lotte4.dto.OrderDTO;
import com.lotte4.dto.OrderItemsDTO;
import com.lotte4.dto.ProductVariantsDTO;
import com.lotte4.dto.mongodb.UserLogDTO;
import com.lotte4.entity.Cart;
import com.lotte4.security.MyUserDetails;
import com.lotte4.service.CartService;
import com.lotte4.service.ProductService;
import com.lotte4.service.mongodb.UserLogService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final ProductService productService;
    private final UserLogService userLogService;
    private final CartService cartService;

    // 공통 메서드
    private void recordUserLog(String eventType, Integer prodId, String keyword, Integer price, Integer rating, Integer quantity) {
        // Authentication을 통한 사용자 ID 가져오기 (로그인 시에만 값이 있음)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = null;

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof MyUserDetails) {
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            userId = userDetails.getUsername(); // UID 가져오기
        }

        // 검색 이벤트는 UID 없이도 저장, 다른 이벤트는 UID가 있을 때만 저장
        if ("search".equals(eventType) || userId != null) {
            UserLogDTO.UserLogDTOBuilder builder = UserLogDTO.builder()
                    .uid(userId) // UID가 없는 경우 null로 설정됨
                    .eventType(eventType)
                    .timestamp(LocalDateTime.now());

            Optional.ofNullable(prodId).ifPresent(builder::prodId);
            Optional.ofNullable(keyword).ifPresent(builder::keyword);
            Optional.ofNullable(price).ifPresent(builder::price);
            Optional.ofNullable(rating).ifPresent(builder::rating);
            Optional.ofNullable(quantity).ifPresent(builder::quantity);

            UserLogDTO userLogDTO = builder.build();
            userLogService.insertLog(userLogDTO);
            System.out.println("Log recorded for event: " + eventType + (userId != null ? " by user: " + userId : "") + (keyword != null ? " with keyword: " + keyword : ""));
        }
    }

    // 상품 보기
    @AfterReturning("execution(* com.lotte4.controller.pagecontroller.ProductController.view(..)) && args(prodId, ..)")
    public void logAfterProductView(Integer prodId) {
        recordUserLog("view", prodId, null, null, null, null); // view 이벤트에 대한 로그 기록
    }
    // 장바구니 입력
    @AfterReturning("execution(* com.lotte4.controller.pagecontroller.ProductController.addCart(..)) && args(cartResponseDTO, principal)")
    public void logAfterAddCart(CartResponseDTO cartResponseDTO, Principal principal) {

        Integer variantId = cartResponseDTO.getProductVariants().get(0);
        List<Integer> counts = cartResponseDTO.getCounts();

        int quantity = counts.stream().mapToInt(Integer::intValue).sum();
        // cartResponseDTO엔 productID가 없습니다
        Integer prodId = productService.findProductVariantById(variantId).getProduct().getProductId();

        recordUserLog("add_cart", prodId, null, null, null, quantity);
    }

    // 장바구니 삭제
    @Before("execution(* com.lotte4.service.CartService.deleteCartItems(..)) && args(cartId)")
    public void logAfterDeleteCart(int cartId) {
        Cart cart = cartService.selectCartById(cartId);
        Integer prodId = cart.getProductVariants().getProduct().getProductId();
        recordUserLog("delete_cart", prodId, null, null, null, null);
    }
    // 주문 입력
    @AfterReturning("execution(* com.lotte4.service.OrderService.insertOrder(..)) && args(orderDTO)")
    public void logAfterInsertOrder(OrderDTO orderDTO) {
        for (OrderItemsDTO orderItemsDTO : orderDTO.getOrderItems()) {

            Integer prodId = orderItemsDTO.getProductVariants().getVariant_id();  // 각 제품의 ID 가져오기
            Integer quantity = orderItemsDTO.getCount();  // 각 제품의 수량 가져오기
            recordUserLog("order", prodId, null, null, null, quantity);
        }
    }
    // 검색 후 로그 기록
    @AfterReturning("execution(* com.lotte4.controller.pagecontroller.ProductController.search(..)) && args(keyword, filters, minPrice, maxPrice, type, ..)")
    public void logAfterSearch(String keyword, List<String> filters, Integer minPrice, Integer maxPrice, String type) {
        recordUserLog("search", null, keyword, null, null, null);
    }
}