package com.lotte4.controller.pagecontroller.CSB;

import com.lotte4.dto.MemberInfoDTO;
import com.lotte4.dto.OrderDTO;
import com.lotte4.dto.OrderItemsDTO;
import com.lotte4.dto.ProductVariantsDTO;
import com.lotte4.entity.ProductVariants;
import com.lotte4.repository.ProductVariantsRepository;
import com.lotte4.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@AllArgsConstructor
@RestController
public class OrderRestController {

    private final OrderService orderService;
    private final ProductVariantsRepository productVariantsRepository;


    @PostMapping("/product/order/save")
    public ResponseEntity<Map<String, Object>> insertOrder(@RequestBody Map<String, Object> orderData, HttpSession session) {

        try {
            OrderDTO orderDTO = new OrderDTO();

            orderDTO.setPayment(orderData.get("Payment") != null ? Integer.parseInt(orderData.get("Payment").toString()) : 0);
            orderDTO.setStatus(orderData.get("Status") != null ? Integer.parseInt(orderData.get("Status").toString()) : 0);
            orderDTO.setTotalPrice(orderData.get("totalPrice") != null ? Integer.parseInt(orderData.get("totalPrice").toString()) : 0);
            orderDTO.setRecipZip(orderData.get("recipZip") != null ? orderData.get("recipZip").toString() : "");
            orderDTO.setRecipAddr1(orderData.get("recipAddr1") != null ? orderData.get("recipAddr1").toString() : "");
            orderDTO.setRecipAddr2(orderData.get("recipAddr2") != null ? orderData.get("recipAddr2").toString() : "");
            orderDTO.setRecipHp(orderData.get("recipHp") != null ? orderData.get("recipHp").toString() : "");
            orderDTO.setRecipName(orderData.get("recipName") != null ? orderData.get("recipName").toString() : "");
            orderDTO.setUsePoint(orderData.get("usePoint") != null ? Integer.parseInt(orderData.get("usePoint").toString()) : 0);
            orderDTO.setContent(orderData.get("content") != null ? orderData.get("content").toString() : null);
            log.info("OrderDTO content after setting: " + orderDTO.getContent());

            orderDTO.setBuyDate(orderData.get("buyDate") != null ? LocalDateTime.parse(orderData.get("buyDate").toString()) : LocalDateTime.now());

            if (orderData.containsKey("content")) {
                orderDTO.setContent(orderData.get("content").toString());
                log.info("Content set in OrderDTO: " + orderDTO.getContent());
            } else {
                log.info("No content found in request data");
            }


            if (orderData.get("memberInfo") != null) {
                Map<String, Object> memberInfoMap = (Map<String, Object>) orderData.get("memberInfo");
                MemberInfoDTO memberInfoDTO = new MemberInfoDTO();
                memberInfoDTO.setMemberInfoId(memberInfoMap.get("memberInfoId") != null ? Integer.parseInt(memberInfoMap.get("memberInfoId").toString()) : 0);
                orderDTO.setMemberInfo(memberInfoDTO);
            }

            // OrderItems 설정 (null 확인 후 처리)
            if (orderData.get("orderItems") != null) {
                List<Map<String, Object>> orderItemsList = (List<Map<String, Object>>) orderData.get("orderItems");
                List<OrderItemsDTO> orderItemsDTOList = new ArrayList<>();

                for (Map<String, Object> orderItemMap : orderItemsList) {
                    OrderItemsDTO orderItemsDTO = new OrderItemsDTO();
                    orderItemsDTO.setOrderItemId(orderItemMap.get("orderItemId") != null ? Integer.parseInt(orderItemMap.get("orderItemId").toString()) : 0);
                    orderItemsDTO.setCount(orderItemMap.get("count") != null ? Integer.parseInt(orderItemMap.get("count").toString()) : 0);
                    orderItemsDTO.setOriginPrice(orderItemMap.get("originPrice") != null ? Integer.parseInt(orderItemMap.get("originPrice").toString()) : 0);
                    orderItemsDTO.setOriginDiscount(orderItemMap.get("originDiscount") != null ? Integer.parseInt(orderItemMap.get("originDiscount").toString()) : 0);
                    orderItemsDTO.setOriginPoint(orderItemMap.get("originPoint") != null ? Integer.parseInt(orderItemMap.get("originPoint").toString()) : 0);
                    orderItemsDTO.setDeliveryFee(orderItemMap.get("deliveryFee") != null ? Integer.parseInt(orderItemMap.get("deliveryFee").toString()) : 0);

                    // ProductVariants 설정 (DB 조회를 통해 모든 필드를 채움)
                    if (orderItemMap.get("productVariants") != null) {
                        Map<String, Object> productVariantsMap = (Map<String, Object>) orderItemMap.get("productVariants");
                        int variantId = productVariantsMap.get("variant_id") != null ? Integer.parseInt(productVariantsMap.get("variant_id").toString()) : 0;

                        Optional<ProductVariants> productVariantsOptional = productVariantsRepository.findById(variantId);
                        if (productVariantsOptional.isPresent()) {
                            ProductVariants productVariants = productVariantsOptional.get();
                            ProductVariantsDTO productVariantsDTO = new ProductVariantsDTO();
                            productVariantsDTO.setVariant_id(productVariants.getVariant_id());
                            productVariantsDTO.setSku(productVariants.getSku());
                            productVariantsDTO.setPrice(productVariants.getPrice());
                            productVariantsDTO.setStock(productVariants.getStock());
                            productVariantsDTO.setOptions(productVariants.getOptions());
                            productVariantsDTO.setUpdated_at(productVariants.getUpdated_at());
                            orderItemsDTO.setProductVariants(productVariantsDTO);
                        }
                    }
                    orderItemsDTOList.add(orderItemsDTO);
                }
                orderDTO.setOrderItems(orderItemsDTOList);
            }

            session.setAttribute("orderContent", orderDTO.getContent());
            log.info("Order content stored in session: " + session.getAttribute("orderContent"));

            // OrderService에 orderDTO와 content 전달
            orderService.insertOrder(orderDTO);
            log.info("Insert Order: " + orderDTO);



            Map<String, Object> response = Map.of("success", true, "message", "주문 저장 완료");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Order insert failed", e);
            Map<String, Object> response = Map.of("success", false, "message", "주문 저장 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
}
