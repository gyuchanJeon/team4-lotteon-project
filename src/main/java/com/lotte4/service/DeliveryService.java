package com.lotte4.service;

import com.lotte4.dto.DeliveryDTO;
import com.lotte4.dto.OrderDTO;
import com.lotte4.entity.Delivery;
import com.lotte4.entity.Order;
import com.lotte4.entity.OrderItems;
import com.lotte4.repository.DeliveryRepository;
import com.lotte4.repository.OrderItemsRepository;
import com.lotte4.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


/*
     날짜 : 2024/10/30
     이름 : 조수빈
     내용 : DeliveryService 생성
*/


@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class DeliveryService {

    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderService orderService;
    private final ModelMapper getModelMapper;


    public DeliveryDTO saveDelivery(int orderId, String deliveryCompany, String deliveryWaybill) {
        // Order 확인
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new IllegalArgumentException("해당 주문을 찾을 수 없습니다. Order ID: " + orderId);
        }

        Order order = optionalOrder.get();

        // OrderItems 확인
        Optional<OrderItems> optionalOrderItems = orderItemsRepository.findFirstByOrderOrderId(orderId);
        if (optionalOrderItems.isEmpty()) {
            throw new IllegalArgumentException("해당 OrderItem을 찾을 수 없습니다. Order ID: " + orderId);
        }

        OrderItems orderItems = optionalOrderItems.get();

        // Delivery 찾거나 생성
        Delivery delivery = deliveryRepository.findByOrderItem_OrderItemId(orderItems.getOrderItemId())
                .orElse(new Delivery());

        // Delivery 정보 설정
        delivery.setDeliveryCompany(deliveryCompany);
        delivery.setDeliveryWaybill(deliveryWaybill);
        delivery.setWaybillDate(LocalDateTime.now());
        delivery.setOrderItem(orderItems);

        // Delivery 저장
        delivery = deliveryRepository.save(delivery);

        // OrderItems 상태값 업데이트
        orderItems.setStatus(2); // 2: 배송 처리를 배송 중으로 변경
        orderItemsRepository.save(orderItems);

        // DeliveryDTO 반환 데이터 생성
        DeliveryDTO savedDeliveryDTO = new DeliveryDTO();
        savedDeliveryDTO.setDeliveryId(delivery.getDeliveryId());
        savedDeliveryDTO.setDeliveryCompany(delivery.getDeliveryCompany());
        savedDeliveryDTO.setDeliveryWaybill(delivery.getDeliveryWaybill());
        savedDeliveryDTO.setWaybillDate(delivery.getWaybillDate());

        return savedDeliveryDTO;
    }

    public String getDeliveryContentByOrderItem(OrderItems orderItems) {
        Delivery delivery = deliveryRepository.findByOrderItem(orderItems);
        return delivery.getContent();
    }

}
