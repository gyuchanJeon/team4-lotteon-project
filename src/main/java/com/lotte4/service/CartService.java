package com.lotte4.service;


import com.lotte4.dto.*;
import com.lotte4.dto.admin.config.InfoDTO;
import com.lotte4.entity.Cart;
import com.lotte4.entity.ProductCate;
import com.lotte4.entity.ProductVariants;
import com.lotte4.entity.User;
import com.lotte4.repository.CartRepository;
import com.lotte4.repository.ProductVariantsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;
/*
     날짜 : 2024/10/30
     이름 : 강은경
     내용 : CartService 생성

     수정이력
      - 2024/10/30 강은경 - cart insert 하는 메서드 추가
      - 2024/11/02 조수빈 - cart select 하는 메서드 추가
*/
@Log4j2
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductVariantsRepository productVariantsRepository;
    private final ModelMapper modelMapper;


    // 사용자 아이디와 선택된 장바구니 항목으로 CartDTO 목록 조회
    public List<CartDTO> getCartItemsByIds(String uid, List<Map<String, Object>> selectedCartItems) {
        // 선택된 장바구니 항목에서 cartId와 count 추출
        List<Integer> cartIds = selectedCartItems.stream()
                .map(item -> (Integer) item.get("cartId"))
                .collect(Collectors.toList());

        // 사용자의 선택된 cartIds에 포함된 장바구니 항목 조회 후 DTO로 매핑
        return cartRepository.findByUserUidAndCartIdIn(uid, cartIds).stream()
            .map(cart -> {
                CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

                // 선택된 항목의 count 값을 매칭하여 설정
                selectedCartItems.stream()
                        .filter(item -> item.get("cartId").equals(cart.getCartId()))
                        .findFirst()
                        .ifPresent(item -> cartDTO.setCount((Integer) item.get("count")));

                return cartDTO;
            })
            .collect(Collectors.toList());
    }


    // 장바구니 목록 select
    public List<CartDTO> getCartByUserId(String uid) {
        List<CartDTO> cartDTOList = new ArrayList<>();

        cartRepository.findByUser_Uid(uid)
                .orElse(Collections.emptyList())
                .forEach(cart -> cartDTOList.add(modelMapper.map(cart, CartDTO.class)));

        return cartDTOList;
    }

    // TODO : LoggingAspect 추가
    // 장바구니 삭제
    public void deleteCartItems(int cartId){
        cartRepository.deleteById(cartId); // cartId로 cart 삭제
    }

    // TODO : 사용자 행동수집 type: order , uid 와 함께 mongoDB insert
    // cart insert
    public List<Cart> insertCart(CartResponseDTO cartResponseDTO) {
        User user = cartResponseDTO.getUser();
        List<Cart> savedCarts = new ArrayList<>();

        for (int i = 0; i < cartResponseDTO.getProductVariants().size(); i++) {
            int variantId = cartResponseDTO.getProductVariants().get(i);
            int count = cartResponseDTO.getCounts().get(i);

            // variant_id로 ProductVariants 조회
            ProductVariants productVariant = productVariantsRepository.findById(variantId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid variant ID: " + variantId));

            // 기존 장바구니에 동일한 상품이 있는지 확인
            Optional<Cart> existingCartOptional = cartRepository.findByUserUidAndProductVariantId(
                    user.getUid(),
                    productVariant.getVariant_id()
            );

            if (existingCartOptional.isPresent()) {
                // 기존 상품이 있으면 count만 업데이트
                Cart existingCart = existingCartOptional.get();
                existingCart.setCount(existingCart.getCount() + count);
                savedCarts.add(cartRepository.save(existingCart));
                log.info("Updated existingCart : " + existingCart);
            } else {
                // 새로운 Cart 객체 생성 및 저장
                Cart newCart = Cart.builder()
                        .user(user)
                        .productVariants(productVariant)
                        .count(count)
                        .build();
                savedCarts.add(cartRepository.save(newCart));
                log.info("Saved newCart : " + newCart);
            }
        }
        return savedCarts;
    }

    // cart count update
    public void updateCartItem(Integer cartId, Integer count) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid cart ID: " + cartId));
        cart.setCount(count);
        // count 저장
        cartRepository.save(cart);
    }


    // 2024.11.07 황수빈 - 사용자 패턴분석을 위해 메서드 추가
    public Cart selectCartById(int id) {
        return cartRepository.findById(id).orElse(null);
    }


}
