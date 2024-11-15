package com.lotte4.service;

import com.lotte4.dto.*;
import com.lotte4.entity.*;
import com.lotte4.repository.*;
import com.lotte4.security.MyUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
/*
     날짜 : 2024/10/30
     이름 : 조수빈
     내용 : orderService 생성




     추후 작업내용 11.04 이후 업데이트 하는 메서드 추가(내부에서 돌릴것인가? or 백단에서 돌릴것인가?)
*/

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {


    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductVariantsRepository productVariantsRepository;
    private final DeliveryRepository deliveryRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final PointRepository pointRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final BestProductService bestProductService;
    private final CouponIssuedRepository couponIssuedRepository;



    private final EntityManager entityManager;

    private final UserService userService;
    private final com.lotte4.controller.pagecontroller.CustomErrorController customErrorController;

    public List<OrderDTO> getOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orders) {
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
            orderDTOs.add(orderDTO);
        }
        return orderDTOs;
    }


    //상품 단품 구매 건(조회부분)
    public OrderDirectBuyDTO getProduct(CartResponseDTO cartResponseDTO) {
        OrderDirectBuyDTO orderDirectBuyDTO = new OrderDirectBuyDTO();

        List<Integer> ids = cartResponseDTO.getProductVariants();
        List<Integer> counts = cartResponseDTO.getCounts();

        List<ProductVariants> productVariantsList = productVariantsRepository.findByVariantIdIn(ids);

        Map<ProductVariants, Integer> variantCountMap = new HashMap<>();
        for (int i = 0; i < productVariantsList.size(); i++) {
            ProductVariants productVariants = productVariantsList.get(i);
            int count = counts.get(i);
            variantCountMap.put(productVariants, count);
        }

        orderDirectBuyDTO.setVariantCountMap(variantCountMap);

        log.info("orderDirectBuyDTO: " + orderDirectBuyDTO);
        return orderDirectBuyDTO;
    }

    // 2024-11-04 수정 완료
    public void insertOrder(OrderDTO orderDTO) {
        log.info("insertOrder: " + orderDTO);
        // 로그인된 사용자 UID 가져오기
        String uid = getLoggedInUserUid();
        log.info("Logged-in user UID: " + uid);

        // 사용자 정보 조회
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자를 찾을 수 없습니다. UID: " + uid));
        log.info("Logged-in user: " + user);

        // 사용자의 현재 포인트 확인
        int userPoints = user.getMemberInfo().getPoint();
        log.info("User points: " + userPoints);


        Order order = new Order();
        order.setPayment(orderDTO.getPayment());
        order.setStatus(orderDTO.getStatus());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setRecipZip(orderDTO.getRecipZip());
        order.setRecipAddr1(orderDTO.getRecipAddr1());
        order.setRecipAddr2(orderDTO.getRecipAddr2());
        order.setRecipHp(orderDTO.getRecipHp());
        order.setRecipName(orderDTO.getRecipName());
        order.setUsePoint(orderDTO.getUsePoint());
        order.setBuyDate(orderDTO.getBuyDate());


        if (orderDTO.getMemberInfo() != null) {
            int memberInfoId = orderDTO.getMemberInfo().getMemberInfoId();
            Optional<MemberInfo> memberInfoOptional = memberInfoRepository.findById(memberInfoId);
            memberInfoOptional.ifPresent(order::setMemberInfo);
        }

        if (orderDTO.getOrderItems() != null) {
            for (OrderItemsDTO orderItemsDTO : orderDTO.getOrderItems()) {
                OrderItems orderItems = new OrderItems();
                orderItems.setCount(orderItemsDTO.getCount());
                orderItems.setDeliveryFee(orderItemsDTO.getDeliveryFee());
                orderItems.setOriginDiscount(orderItemsDTO.getOriginDiscount());
                orderItems.setOriginPoint(orderItemsDTO.getOriginPoint());
                orderItems.setOriginPrice(orderItemsDTO.getOriginPrice());
                orderItems.setItemOption(orderItemsDTO.getProductVariants().getSku());
                orderItems.setStatus(1);
                orderItems.setVariantId(orderItemsDTO.getVariantId());

                // ProductVariants 설정
                if (orderItemsDTO.getProductVariants() != null) {
                    int variantId = orderItemsDTO.getProductVariants().getVariant_id();
                    Optional<ProductVariants> productVariantsOptional = productVariantsRepository.findById(variantId);


                    productVariantsOptional.ifPresent(productVariants -> {
                        // OrderItems에 ProductVariants 엔티티를 직접 설정
                        orderItems.setVariantId(variantId);
                        log.info("ProductVariants 엔티티 설정 완료: " + productVariants);
                        productVariants.setVariant_id(variantId); // 원하는 variantId 설정
                        order.setProductVariants(productVariants); // order에 설정
                        //Redis에 판매 순위 업데이트 - 강중원 11.08
                        Product product = productVariants.getProduct();
                        ProductBestDTO bestDTO = modelMapper.map(product, ProductBestDTO.class);
                        bestProductService.updateSalesInRedis(bestDTO, orderItemsDTO.getCount());
                    });
                }

                // OrderItems를 Order에 추가
                order.addOrderItem(orderItems);

                log.info("OrderDTO content before setting in Delivery: " + orderDTO.getContent());
                // Delivery 생성 및 설정
                Delivery delivery = new Delivery();
                delivery.setOrderItem(orderItems);
                delivery.setDeliveryDate(LocalDateTime.now().plusDays(3));
                delivery.setContent(orderDTO.getContent());
                log.info("16722757257"+delivery.toString());
                deliveryRepository.save(delivery);

                if (orderDTO.getUsePoint() != 0){
                    Point point = new Point();
                    point.setMemberInfo(order.getMemberInfo());
                    point.setPoint(orderDTO.getUsePoint());
                    point.setPointDate(LocalDateTime.now());

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
                    String formattedDate = LocalDateTime.now().format(formatter);

                    point.setPointName(formattedDate + " " + order.getProductVariants().getProduct().getName() + " " + order.getProductVariants().getSku() + "물품 구매건");
                    point.setPresentPoint(userPoints - orderDTO.getUsePoint());
                    point.setType("차감");
                    pointRepository.save(point);
                }
            }
        }
        // 최종 Order 저장
        orderRepository.save(order);
        log.info("주문저장 로그 " + order);
    }

    public List<OrderDTO> selectAllOrders(){
        List<Order> orders = orderRepository.findAll();
        log.info("selectAllOrders = " + orders);
        List<OrderDTO> orderDTOS = new ArrayList<>();

        for (Order order : orders) {
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

            log.info("Selected order: " + order);
            // ProductVariants 처리 11월 04일 이후 아이디는 principal에서 받아와야함
            if (orderDTO.getProductVariants() == null) {
                ProductVariants productVariants = ProductVariants.builder()
                        .variant_id(1)
                        .build();
                orderDTO.setProductVariants(ProductVariantsDTO.builder().build());
            } else {
                orderDTO.setProductVariants(orderDTO.getProductVariants());
            }

            orderDTO.setMemberInfo(order.getMemberInfo().toDTO());

            if (order.getProductVariants() != null && order.getProductVariants().getProduct() != null) {
                ProductDTO productDTO =new ProductDTO(order.getProductVariants().getProduct());
                log.info("ProductDTO: " + productDTO);
            } else {
                log.warn("Product is null for order ID: " + order.getOrderId());
            }

            orderDTOS.add(orderDTO);
        }

        log.info("select ALL Orders: " + orderDTOS);
        return orderDTOS;
    }

    // 해당 사용자의 주문 목록을 최신순으로 조회
    public List<OrderDTO> selectAllByMemberInfoOrderByDateDesc(MemberInfo memberInfo) {
        List<Order> orders = orderRepository.findAllByMemberInfoOrderByBuyDateDesc(memberInfo);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : orders) {
            OrderDTO orderDTO = new OrderDTO(order);
            orderDTO.setOrderItems(getMissingProductVariants(orderDTO.getOrderItems()));
            orderDTOS.add(orderDTO);
        }
        return orderDTOS;
    }

    // 1. 상대적인 기간 조회
    public List<OrderDTO> getOrdersByRelativePeriod(String period, MemberInfo memberInfo) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetDate;

        switch (period.toLowerCase()) {
            case "week":
                targetDate = now.minusDays(7);
                break;
            case "15days":
                targetDate = now.minusDays(15);
                break;
            case "month":
                targetDate = now.minusMonths(1);
                break;
            default:
                throw new IllegalArgumentException("Invalid period specified");
        }

        List<Order> orders = orderRepository.findAllByMemberInfoAndBuyDateAfterOrderByBuyDateDesc(memberInfo, targetDate);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : orders) {
            OrderDTO orderDTO = new OrderDTO(order);
            orderDTO.setOrderItems(getMissingProductVariants(orderDTO.getOrderItems()));
            orderDTOS.add(orderDTO);
        }
        return orderDTOS;
    }

    // 2. 특정 월 단위 조회
    public List<OrderDTO> getOrdersByMonth(MemberInfo memberInfo, int month, int year) {
        List<Order> orders = orderRepository.findAllByMemberInfoAndMonthAndYear(memberInfo ,month, year);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : orders) {
            OrderDTO orderDTO = new OrderDTO(order);
            orderDTO.setOrderItems(getMissingProductVariants(orderDTO.getOrderItems()));
            orderDTOS.add(orderDTO);
        }
        return orderDTOS;
    }

    // 3. 사용자 지정 기간 조회
    public List<OrderDTO> getOrdersByCustomDateRange(MemberInfo memberInfo, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findAllByMemberInfoAndBuyDateBetweenOrderByBuyDateDesc(memberInfo, startDate, endDate);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (Order order : orders) {
            OrderDTO orderDTO = new OrderDTO(order);
            orderDTO.setOrderItems(getMissingProductVariants(orderDTO.getOrderItems()));
            orderDTOS.add(orderDTO);
        }
        return orderDTOS;
    }



    public List<Order> selectLastOrder() {
        Pageable pageable = PageRequest.of(0, 1);
        List<Order> result = orderRepository.findOrdersWithDetailsAndDelivery(pageable);
        log.info("123123123"+ result);
        return result;
    }
    public List<UserDTO> selectAllUser(){
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            userDTOS.add(userDTO);
        }
        log.info("select ALL User: " + userDTOS);
        return userDTOS;
    }



    public Map<Integer, Integer> getOrderItemCounts(List<Order> orders) {
        List<Integer> orderIds = orders.stream()
                .map(Order::getOrderId)
                .collect(Collectors.toList());

        List<Map<String, Object>> results = orderRepository.countItemsByOrderIds(orderIds);

        Map<Integer, Integer> orderItemCounts = new HashMap<>();
        for (Map<String, Object> result : results) {
            Integer orderId = (Integer) result.get("orderId");
            Integer itemCount = ((Number) result.get("itemCount")).intValue();
            orderItemCounts.put(orderId, itemCount);
        }
        return orderItemCounts;
    }


    public List<ProductVariants> selectProducts(){
        return productVariantsRepository.findAllProductVariants();
    }


    public Page<OrderProductDTO> getOrdersWithProducts(Pageable pageable, String sortBy) {
        Sort sort;
        switch (sortBy) {
            case "orderId":
                sort = Sort.by(Sort.Direction.DESC, "orderId");
                break;
            case "buyerName": // 주문자명
                sort = Sort.by(Sort.Direction.ASC, "memberInfo.name");
                break;
            case "buyerUid": // 주문자 아이디
                sort = Sort.by(Sort.Direction.ASC, "memberInfo.uid");
                break;
            default: // 기본 정렬: 구매 날짜 내림차순
                sort = Sort.by(Sort.Direction.DESC, "buyDate");
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "buyDate"));
        Page<Order> pagedOrders = orderRepository.findAll(sortedPageable);
        List<OrderItems> orderItems = getAllOrderItems();
        List<ProductVariants> productVariants = selectProducts();

        Map<Integer, ProductVariants> productMap = productVariants.stream()
                .collect(Collectors.toMap(ProductVariants::getVariant_id, product -> product));

        List<OrderProductDTO> orderProductDTOs = pagedOrders.getContent().stream().map(order -> {
            // Order에 연결된 OrderItems를 MatchedOrderItemDTO로 변환
            List<MatchedOrderItemDTO> matchedItems = orderItems.stream()
                    .filter(item -> item.getOrder() != null && item.getOrder().getOrderId() == order.getOrderId())
                    .map(item -> {
                        ProductVariants product = productMap.get(item.getVariantId());
                        return product != null ? new MatchedOrderItemDTO(item, product) : null;
                    })
                    .filter(Objects::nonNull) // null 값 제거
                    .collect(Collectors.toList());

            return new OrderProductDTO(order, matchedItems);
        }).collect(Collectors.toList());

        PageImpl<OrderProductDTO> orderProductDTOS = new PageImpl<>(orderProductDTOs, pageable, pagedOrders.getTotalElements());
        return orderProductDTOS;
    }



    public List<OrderProductDTO> getOrdersWithProductsByOrderId(int orderId) {
        List<Order> orders = getAllOrders();
        List<OrderItems> orderItems = getAllOrderItems();
        List<ProductVariants> productVariants = selectProducts();

        Map<Integer, ProductVariants> productMap = productVariants.stream()
                .collect(Collectors.toMap(ProductVariants::getVariant_id, product -> product));

        List<OrderProductDTO> result = new ArrayList<>();

        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                List<MatchedOrderItemDTO> matchedItems = new ArrayList<>();
                for (OrderItems item : orderItems) {
                    if (item.getOrder() != null && item.getOrder().getOrderId() == orderId) {
                        ProductVariants product = productMap.get(item.getVariantId());
                        if (product != null) {
                            matchedItems.add(new MatchedOrderItemDTO(item, product));
                        }
                    }
                }
                result.add(new OrderProductDTO(order, matchedItems));
            }
        }
        return result;
    }


    public OrderProductDTO getOrderDetailsById(int orderId) {
        try {
            List<Order> orders = getAllOrders();
            List<OrderItems> orderItems = getAllOrderItems();
            List<ProductVariants> productVariants = selectProducts();

            Map<Integer, ProductVariants> productMap = productVariants.stream()
                    .collect(Collectors.toMap(ProductVariants::getVariant_id, product -> product));

            for (Order order : orders) {
                if (order.getOrderId() == orderId) {
                    List<MatchedOrderItemDTO> matchedItems = new ArrayList<>();
                    for (OrderItems item : orderItems) {
                        if (item.getOrder() != null && item.getOrder().getOrderId() == orderId) {
                            ProductVariants product = productMap.get(item.getVariantId());
                            if (product != null) {
                                matchedItems.add(new MatchedOrderItemDTO(item, product));
                            }
                        }
                    }
                    return new OrderProductDTO(order, matchedItems);
                }
            }
            log.warn("orderId: {}", orderId);
            return null;
        } catch (Exception e) {
            log.error("details orderId: {}", orderId, e);
            throw new RuntimeException("order details", e);
        }
    }



    //포인트값 조회
    public UserPointCouponDTO selectUserPoint(String uid) {
        int memberInfoId = userService.getMemberInfoIdByUid(uid);
        Integer totalPoints = pointRepository.findTotalPointsByMemberInfoId(memberInfoId);
        return new UserPointCouponDTO(totalPoints);
    }


    //사용자 가지고있는 쿠폰 값
    public List<CouponIssued> selectUserCoupon(String uid) {
        Optional<User> userInfo = userService.getUserByUid(uid);

        if (userInfo.isEmpty()) {
            log.warn("No user found with UID: " + uid);
            return Collections.emptyList();
        }
        int userId = userInfo.get().getUserId();
        log.info("userId = " + userId);
        List<CouponIssued> result = couponIssuedRepository.findByUserUid(userId);

        log.info("Fetched Coupons: " + result);
        return result;
    }


    public List<OrderItems> getAllOrderItems(){
        return orderRepository.findAllOrderItems();
    }


    public List<Order> getAllOrders(){
        return orderRepository.findAllOrders();
    }





    public void updateStock(){
        List<Order> completedOrders = orderRepository.findByStatus(1);
        for (Order order : completedOrders) {
            List<OrderItems> orderItemsList = orderItemsRepository.findByOrderId(order.getOrderId());
            for (OrderItems orderItem : orderItemsList) {
                int variantId = orderItem.getVariantId();
                int count = orderItem.getCount();
                Optional<ProductVariants> productVariantOptional = productVariantsRepository.findById(variantId);
                if (productVariantOptional.isPresent()) {
                    ProductVariants productVariants = productVariantOptional.get();
                    productVariants.setStock(productVariants.getStock() - count);
                    if(productVariants.getStock() <= 0){
                        productVariants.setStock(0);
                    }else{
                        productVariantsRepository.save(productVariants);
                    }
                } else {
                    log.warn("variantId 못 찾음: " + variantId);
                }
            }
        }
    }

    public OrderDTO selectRecentOrder(MemberInfo memberInfo) {
        Order recentOrder = orderRepository.findFirstByMemberInfoOrderByBuyDateDesc(memberInfo);
        return new OrderDTO(recentOrder);
    }

    // orderItems 의 variantId를 받아와 조회하고 DTO로 변환 후 리스트에 담아 반환
    public List<OrderItemsDTO> getMissingProductVariants(List<OrderItemsDTO> orderItems) {

        for (OrderItemsDTO orderItem : orderItems) {
            Optional<ProductVariants> optional = productVariantsRepository.findById(orderItem.getVariantId());
            if (optional.isPresent()) {
                ProductVariants productVariants = optional.get();
                orderItem.setProductVariants(new ProductVariantsDTO(productVariants));
            }
        }
        return orderItems;
    }

    public int findAllByDay(LocalDate date) {
        return orderRepository.findAllByDay(date);
    }

    public int findPriceSumByDay(LocalDate date) {
        return orderRepository.findPriceSumByDay(date).orElse(0);
    }

    public int findAllItemByDayWithStatus(LocalDate date, int status) {
        return orderItemsRepository.findAllByDayWithStatus(date, status);
    }

    public int findAllByDayWithStatus(LocalDate date, int status) {
        return orderRepository.findAllByDayWithStatus(date, status);
    }

    public int findAllByDayWithStatusFrom5Day(int from,LocalDate today, int status) {
        LocalDate startDay = today.minusDays(from);
        return orderItemsRepository.findAllByLastFiveDaysWithStatus(startDay, today, status);
    }




    public Page<Delivery> getAllDeliverys(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return deliveryRepository.findAllByOrderByorderIdDesc(pageable);
    }


    // myPage용
    public void confirmOrderItem(Integer orderItemId) {
        OrderItems orderItem = orderItemsRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 OrderItem입니다."));
        orderItem.setStatus(4);
        orderItemsRepository.save(orderItem);
    }

    public void processReturnOrder(int orderItemId) {
        try {
            orderItemsRepository.returnProc(orderItemId);
            log.info("Return process completed in database for OrderItem ID: " + orderItemId);
        } catch (Exception e) {
            log.error("Error during database return process for OrderItem ID: " + orderItemId, e);
            throw e;
        }
    }

    public void processChangeOrder(int orderItemId) {
        try {
            orderItemsRepository.changeProc(orderItemId);
            log.info("Change process completed in database for OrderItem ID: " + orderItemId);
        } catch (Exception e) {
            log.error("Error during database change process for OrderItem ID: " + orderItemId, e);
            throw e;
        }
    }

    private String getLoggedInUserUid() {
        // 현재 인증된 사용자 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof MyUserDetails) {
            return ((MyUserDetails) principal).getUser().getUid();
        } else {
            throw new IllegalStateException("현재 인증된 사용자 정보를 가져올 수 없습니다.");
        }
    }

    public User getLoggedInUser() {
        String uid = getLoggedInUserUid();
        return userRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. UID: " + uid));
    }
}
