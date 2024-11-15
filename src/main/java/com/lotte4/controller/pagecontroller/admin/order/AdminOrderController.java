package com.lotte4.controller.pagecontroller.admin.order;

import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.lotte4.controller.pagecontroller.CSB.OrderController;
import com.lotte4.dto.*;
import com.lotte4.entity.Delivery;
import com.lotte4.entity.Order;
import com.lotte4.entity.OrderItems;
import com.lotte4.entity.ProductVariants;
import com.lotte4.repository.OrderItemsRepository;
import com.lotte4.repository.OrderRepository;
import com.lotte4.service.DeliveryService;
import com.lotte4.service.OrderService;
import com.lotte4.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;


@Log4j2
@AllArgsConstructor
@Controller
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping("/admin/order/list")
    public String orderList(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "10") String sortBy) {
        List<Order> orders = orderService.getAllOrders();

        Map<Integer, Integer> orderItemCounts = orderService.getOrderItemCounts(orders);
        model.addAttribute("orderItemCounts", orderItemCounts);

        List<UserDTO> users = orderService.selectAllUser();
        model.addAttribute("users", users);

        Page<OrderProductDTO> products = orderService.getOrdersWithProducts(PageRequest.of(page, size), sortBy);
        for (OrderProductDTO product : products) {
            System.out.println("Product: " + product);
            System.out.println("OrderItems: " + product.getOrder().getOrderItems());
        }
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);

        return "/admin/order/list";
    }

    @GetMapping("/admin/order/delivery")
    public String orderDelivery(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size ) {

        Page<Delivery> deliveryPage = orderService.getAllDeliverys(page, size);
        model.addAttribute("deliveries", deliveryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", deliveryPage.getTotalPages());

        model.addAttribute("deliverys" , orderService.getAllDeliverys(page, size));

        return "/admin/order/delivery";
    }





}
