package com.lotte4.controller.pagecontroller.admin.shop;

import com.lotte4.dto.UserDTO;
import com.lotte4.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
public class ShopController {

    private final UserService userService;

    public ShopController(UserService userService) {
        this.userService = userService;
    }

    //상점목록
    @GetMapping("/admin/shop/list")
    public String Adminshoplist(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String searchCategory){

        String role = "seller";
        
        // 검색 조건에 따라 회원 목록을 가져옴
        Page<UserDTO> userList = userService.selectUserListBySeller(role, page, size, keyword, searchCategory);

        // 시작 번호 계산
        long totalElements = userList.getTotalElements();
        int startNo = (int) totalElements - (page * size);

        log.info("userList : " + userList);

        model.addAttribute("userList", userList);
        model.addAttribute("totalPages", userList.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("startNo", startNo);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchCategory", searchCategory);

        return "/admin/shop/list";
    }

    // 매출현황
    @GetMapping("/admin/shop/sales")
    public String Adminshopsales(){
        return "/admin/shop/sales";
    }
}
