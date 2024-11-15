package com.lotte4.controller.pagecontroller.admin.order;


import com.lotte4.dto.*;
import com.lotte4.service.DeliveryService;
import com.lotte4.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Log4j2
@AllArgsConstructor
@RestController
public class AdminOrderRestController {

    private final DeliveryService deliveryService;
    private final OrderService orderService;

    @PostMapping("/admin/order/delivery/save")
    public ResponseEntity<DeliveryDTO> deliveryInsert(
            @RequestParam("orderId") int orderId, // 반드시 "orderId"로 이름 지정
            @RequestBody Map<String, String> deliveryData) {

        String deliveryCompany = deliveryData.get("deliveryCompany");
        String deliveryWaybill = deliveryData.get("deliveryWaybill");

        if (deliveryCompany == null || deliveryWaybill == null) {
            throw new IllegalArgumentException("배송회사 또는 운송장 번호가 전달되지 않았습니다.");
        }

        DeliveryDTO savedDelivery = deliveryService.saveDelivery(orderId, deliveryCompany, deliveryWaybill);

        return ResponseEntity.ok(savedDelivery);
    }

    @GetMapping("/admin/order/list/{orderId}")
    public ResponseEntity<OrderProductDTO> getOrderDetails(@PathVariable int orderId) {
        OrderProductDTO orderDetails = orderService.getOrderDetailsById(orderId);
        if (orderDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        try {
            return ResponseEntity.ok(orderDetails);
        } catch (Exception e) {
            log.error("JSON error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
