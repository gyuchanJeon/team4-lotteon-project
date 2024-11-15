package com.lotte4.repository;

import com.lotte4.dto.OrderWithDetailsDTO;
import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.Order;
import com.lotte4.entity.OrderItems;
import com.lotte4.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/*
     날짜 : 2024/10/30
     이름 : 조수빈
     내용 : OrderRepository 생성

     수정이력
       - 2024-11-08 전규찬 모든 주문을 최신순으로 조회하는 기능 추가 / 기간별 주문 목록 조회 기능 추가

*/

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.delivery ORDER BY o.orderId DESC")
    List<Order> findOrdersWithDetailsAndDelivery(Pageable pageable);

    @Query("SELECT oi FROM OrderItems oi " +
            "JOIN FETCH oi.delivery " +
            "JOIN FETCH oi.order")
    List<OrderItems> findAllOrderItems();

    @Query("SELECT oi FROM OrderItems oi " +
            "JOIN FETCH oi.delivery " +
            "JOIN FETCH oi.order WHERE oi.order.orderId = :orderId")
    List<OrderItems> findAllOrderItemsByOrderId(@Param("orderId") int orderId);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH o.memberInfo m " +
            "order by o.orderId desc")
    List<Order> findAllOrders();

    Order findFirstByMemberInfoOrderByBuyDateDesc(MemberInfo memberInfo);

    @Query("select o from Order o where o.Status = :status")
    List<Order> findByStatus(@Param("status") int status);


    @Query("SELECT o.orderId AS orderId, COUNT(oi) AS itemCount " +
            "FROM Order o JOIN o.orderItems oi " +
            "WHERE o.orderId IN :orderIds " +
            "GROUP BY o.orderId")
    List<Map<String, Object>> countItemsByOrderIds(@Param("orderIds") List<Integer> orderIds);

    // 해당 사용자의 모든 주문을 최신순으로 조회
    List<Order> findAllByMemberInfoOrderByBuyDateDesc(MemberInfo memberInfo);

    // 1. 현재 시점 기준 상대적인 기간 조회
    List<Order> findAllByMemberInfoAndBuyDateAfterOrderByBuyDateDesc(MemberInfo memberInfo, LocalDateTime buyDate);

    // 2. 특정 월 단위로 조회
    @Query("SELECT o FROM Order o WHERE o.memberInfo = :memberInfo AND FUNCTION('MONTH', o.buyDate) = :month AND FUNCTION('YEAR', o.buyDate) = :year ORDER BY o.buyDate DESC")
    List<Order> findAllByMemberInfoAndMonthAndYear(@Param("memberInfo") MemberInfo memberInfo, @Param("month") int month, @Param("year") int year);


    // 3. 사용자 지정 기간 조회
    List<Order> findAllByMemberInfoAndBuyDateBetweenOrderByBuyDateDesc(MemberInfo memberInfo, LocalDateTime buyDate, LocalDateTime buyDate2);

    @Query("SELECT COUNT(oi) " +
            "FROM OrderItems oi " +
            "WHERE oi.order.orderId = :orderId")
    Integer findOrderItemCountByOrderId(@Param("orderId") int orderId);

    @Query("SELECT COUNT(b) FROM Order b WHERE DATE(b.buyDate) = :today")
    int findAllByDay(@Param("today") LocalDate day);

    @Query("SELECT SUM(b.totalPrice) FROM Order b WHERE DATE(b.buyDate) = :today")
    Optional<Integer> findPriceSumByDay(@Param("today") LocalDate day);

    @Query("SELECT COUNT(b) FROM Order b WHERE DATE(b.buyDate) = :today AND b.Status = :status")
    int findAllByDayWithStatus(@Param("today") LocalDate day, @Param("status") int status);


}
