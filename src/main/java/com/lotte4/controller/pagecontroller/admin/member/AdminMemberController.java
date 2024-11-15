package com.lotte4.controller.pagecontroller.admin.member;


import com.lotte4.dto.PointDTO;
import com.lotte4.dto.UserDTO;

import com.lotte4.dto.MemberInfoDTO;
import com.lotte4.dto.PointDTO;
import com.lotte4.dto.UserDTO;
import com.lotte4.service.MemberInfoService;

import com.lotte4.service.PointService;
import com.lotte4.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*
     날짜 : 2024/10/28
     이름 : 강은경
     내용 : AdminMemberController 생성

     수정이력
      - 2024/10/28 강은경 - 관리자 회원목록 기능 검색&페이징 메서드 추가
      - 2024/10/30 황수빈 - @RequiredArgsConstructor 추가, admin/point/list 추가
*/
@Log4j2
@Controller
@RequiredArgsConstructor
public class AdminMemberController {

    private final UserService userService;
    private final PointService pointService;
    private final MemberInfoService memberInfoService;



    @GetMapping("/admin/member/list")
    public String AdminMemberList(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String searchCategory) {
        String role = "member";

        // 검색 조건에 따라 회원 목록을 가져옴
        Page<UserDTO> userList = userService.selectUserListByMember(role, page, size, keyword, searchCategory);

        // 시작 번호 계산
        long totalElements = userList.getTotalElements();
        int startNo = (int) totalElements - (page * size);

        model.addAttribute("userList", userList);
        model.addAttribute("totalPages", userList.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("startNo", startNo);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchCategory", searchCategory);

        return "/admin/member/list";
    }


    @GetMapping("/admin/member/list/{uid}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String uid) {
        UserDTO userDTO = userService.selectUser(uid);
        log.info("userDTO: " + userDTO);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 회원수정
    @PutMapping("/admin/member/update")
    public ResponseEntity<Void> updateMember(@RequestBody MemberInfoDTO memberInfoDTO) {
        log.info("memberInfoDTO: " + memberInfoDTO);
        memberInfoService.updateMember(memberInfoDTO);
        return ResponseEntity.ok().build();
    }

    // 회원 등급 수정
    @PutMapping("/admin/member/update-grades")
    public ResponseEntity<Void> updateGrades(@RequestBody List<MemberInfoDTO> memberGrades) {
        memberInfoService.updateMemberGrades(memberGrades);
        return ResponseEntity.ok().build(); // 200 OK 응답 반환
    }
    @ResponseBody
    @GetMapping("/member/point")
    public ResponseEntity<Page<PointDTO>> pointfinder (@RequestParam(required = false) String searchType,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String type,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);

        // 공통 메서드인 `searchPoints`를 호출하여 모든 조건을 처리
        Page<PointDTO> results = pointService.searchPoints(type, searchType, keyword, pageable);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/admin/member/point")

    public String Adminmemberpoint(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "8") int size) {

        return "/admin/member/point";
    }
}
