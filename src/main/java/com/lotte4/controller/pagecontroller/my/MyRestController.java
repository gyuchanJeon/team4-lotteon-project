package com.lotte4.controller.pagecontroller.my;


import com.lotte4.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@AllArgsConstructor
@RestController
public class MyRestController {

    private OrderService orderService;

    @PostMapping("/my/home/accept")
    public ResponseEntity<String> confirmOrder(@RequestBody Map<String, Integer> requestData) {
        Integer orderItemId = requestData.get("orderItemId");
        log.info("Request Data: " + requestData);
        orderService.confirmOrderItem(orderItemId);
        log.info("orderItemId: " + orderItemId);

        return ResponseEntity.ok("구매확정 처리 완료");
    }

    @PostMapping("/my/home/return")
    public ResponseEntity<String> returnOrder(@RequestBody Map<String, Integer> requestData) {
        Integer orderItemId = requestData.get("orderItemId");
        log.info("Return OrderID: " + orderItemId);

        if (orderItemId == null) {
            log.error("OrderItem ID is null.");
            return ResponseEntity.badRequest().body("OrderItem ID is required.");
        }

        try {
            orderService.processReturnOrder(orderItemId);
            log.info("Return process completed for OrderItem ID: " + orderItemId);
            return ResponseEntity.ok("반품 완료");
        } catch (Exception e) {
            log.error("Error during return process for OrderItem ID: " + orderItemId, e);
            return ResponseEntity.status(500).body("반품 처리 실패");
        }
    }

    @PostMapping("/my/home/change")
    public ResponseEntity<String> changeOrder(@RequestBody Map<String, Integer> requestData) {
        Integer orderItemId = requestData.get("orderItemId");
        log.info("Change OrderID: " + orderItemId);

        if (orderItemId == null) {
            log.error("OrderItem ID is null.");
            return ResponseEntity.badRequest().body("OrderItem ID is required.");
        }

        try {
            orderService.processChangeOrder(orderItemId);
            log.info("Change process completed for OrderItem ID: " + orderItemId);
            return ResponseEntity.ok("교환 완료");
        } catch (Exception e) {
            log.error("Error during change process for OrderItem ID: " + orderItemId, e);
            return ResponseEntity.status(500).body("교환 처리 실패");
        }
    }

}
