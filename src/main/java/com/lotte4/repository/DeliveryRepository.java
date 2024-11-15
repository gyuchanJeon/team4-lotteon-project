package com.lotte4.repository;

import com.lotte4.entity.Delivery;
import com.lotte4.entity.OrderItems;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
     날짜 : 2024/10/30
     이름 : 조수빈
     내용 : DeliveryRepository 생성

     수정이력
       - 2024-10-30 조수빈 : 조회조건을 기존 Jpa받는것 보다 fetch로 받아서 직접적으로 받는게 좋다고 나와서 적용함
       - 2024-11-07 전규찬 : orderItemId로 delivery의 content(배송요청사항) 찾아오는 메서드 추가

*/


//조회 조건은 fetch로 받는게 좋다고 나와서 사용해봄
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Delivery findByOrderItem(OrderItems orderItems);

    @Query("SELECT d FROM Delivery d " +
            "LEFT JOIN d.orderItem oi " +
            "LEFT JOIN oi.order o " +
            "ORDER BY o.orderId DESC")
    Page<Delivery> findAllByOrderByorderIdDesc(Pageable pageable);

    Optional<Delivery> findByOrderItem_OrderItemId(Integer orderItemId);

    @Override
    Page<Delivery> findAll(Pageable pageable);

    Page<Delivery> findByOrderItem_OrderItemId(Integer orderItemId, Pageable pageable);
}