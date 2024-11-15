package com.lotte4.controller.pagecontroller;

import com.lotte4.entity.Banner;
import com.lotte4.service.OrderService;
import com.lotte4.service.UserService;
import com.lotte4.service.VisitorService;
import com.lotte4.service.board.BoardService;
import com.querydsl.core.types.Visitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
/*

    2024.11.07 어제, 오늘 방문자수 가져오는 메서드 추가

 */


@Log4j2
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final VisitorService visitorService;
    private final BoardService boardService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/admin/index")
    public String AdminHome(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        //입금대기
        int todayWaitMoney = orderService.findAllByDayWithStatus(today,0);
        //배송준비
        int todayPreDelivery = orderService.findAllItemByDayWithStatus(today,1);
        //취소요청
        int todayCancelRequest = orderService.findAllItemByDayWithStatus(today,5);;
        //교환요청
        int todayChangeRequest = orderService.findAllItemByDayWithStatus(today,6);;
        //반품요청
        int todayReturnRequest = orderService.findAllItemByDayWithStatus(today,7);;
        //주문수량
        int todayOrder = orderService.findAllByDay(today);
        int yesterdayOrder = orderService.findAllByDay(yesterday);
        //주문금액
        int todayPrice = orderService.findPriceSumByDay(today);
        int yesterdayPrice = orderService.findPriceSumByDay(yesterday);
        //회원가입수
        int todayRegister = userService.findAllByDay(today);
        int yesterdayRegister = userService.findAllByDay(yesterday);
        //방문자수
        int yesterdayVisitor = visitorService.getVisitorCountYesterday();      //어제 방문자수
        int todayVisitor = visitorService.getVisitorCountToday();          //오늘 방문자수
        //문의게시글
        int todayBoard = boardService.countBoardWithDay(today);
        int yesterdayBoard = boardService.countBoardWithDay(yesterday);

        //입금대기
        model.addAttribute("todayWaitMoney", todayWaitMoney);
        //배송준비
        model.addAttribute("todayPreDelivery", todayPreDelivery);
        //취소요청
        model.addAttribute("todayCancelRequest", todayCancelRequest);
        //교환요청
        model.addAttribute("todayChangeRequest", todayChangeRequest);
        //반품요청
        model.addAttribute("todayReturnRequest", todayReturnRequest);
        //주문수량
        model.addAttribute("todayOrder", todayOrder);
        model.addAttribute("yesterdayOrder", yesterdayOrder);
        //주문금액
        model.addAttribute("todayPrice", todayPrice);
        model.addAttribute("yesterdayPrice", yesterdayPrice);
        //회원가입수
        model.addAttribute("yesterdayRegister", yesterdayRegister);
        model.addAttribute("todayRegister", todayRegister);
        //방문자수
        model.addAttribute("yesterdayVisitor", yesterdayVisitor);
        model.addAttribute("todayVisitor", todayVisitor);
        //문의 게시글
        model.addAttribute("yesterdayBoard", yesterdayBoard);
        model.addAttribute("todayBoard", todayBoard);

        return "/admin/index";
    }

    @GetMapping("/admin/index/OrderStickChart")
    @ResponseBody
    public ResponseEntity<?> getOrderStats(@RequestParam("days") int days) {
        Map<String, Object> response = new HashMap<>();

        LocalDate today = LocalDate.now();
        List<LocalDate> dates = today.minusDays(days - 1)
                .datesUntil(today.plusDays(1))
                .collect(Collectors.toList());

        List<Integer> orderCounts = dates.stream()
                .map(date -> orderService.findAllByDay(date))
                .collect(Collectors.toList());

        List<Integer> paymentCounts = dates.stream()
                .map(date -> orderService.findAllByDayWithStatus(date, 1))
                .collect(Collectors.toList());

        List<Integer> cancelCounts = dates.stream()
                .map(date -> orderService.findAllItemByDayWithStatus(date,5))
                .collect(Collectors.toList());

        response.put("dates", dates);
        response.put("orderCounts", orderCounts);
        response.put("paymentCounts", paymentCounts);
        response.put("cancelCounts", cancelCounts);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/index/OrderPieChart")
    @ResponseBody
    public ResponseEntity<?> getOrderPieStats() {
        Map<String, Object> response = new HashMap<>();

        LocalDate today = LocalDate.now();

        List<Integer> Counts = new ArrayList<>();
        int startFrom = 5;
        Counts.add(orderService.findAllByDayWithStatusFrom5Day(startFrom, today,5));
        Counts.add(orderService.findAllByDayWithStatusFrom5Day(startFrom, today,3));
        Counts.add(orderService.findAllByDayWithStatusFrom5Day(startFrom, today,2));
        Counts.add(orderService.findAllByDayWithStatusFrom5Day(startFrom, today,1));

        response.put("Counts", Counts);


        return ResponseEntity.ok(response);
    }

}
