package com.lotte4.controller.pagecontroller;

import com.lotte4.dto.TermsDTO;
import com.lotte4.dto.UserDTO;
import com.lotte4.entity.User;
import com.lotte4.service.TermsService;
import com.lotte4.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/*
     날짜 : 2024/10/31
     이름 : 강은경
     내용 : MemberController 생성

     수정이력
      - 2024/10/31 강은경 - 아이디찾기 & 비밀번호 찾기 메서드 추가
      - 2024/11/03 강은경 - 아이디찾기 & 비밀번호 찾기&변경 메서드 추가
*/

@Log4j2
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final UserService userService;
    private final TermsService termsService;


    @GetMapping("/member/login")
    public String login() {

        log.info("login");
        return "/member/login";
    }

    // 로그인 처리
    @PostMapping("/member/login")
    public String login(HttpServletRequest req, @RequestParam("uid") String uid, @RequestParam("pass") String pass, Model model) {
        log.info("Login attempt: uid = " + uid);

        // UserService를 통해 로그인 처리
        UserDTO user = userService.login(uid, pass);

        if (user != null) {
            // 로그인 성공: 세션에 사용자 정보 저장
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            log.info("Login successful: " + user.getUid());
            return "redirect:/"; // 로그인 후 리다이렉트할 페이지
        } else {
            // 로그인 실패 시 에러 메시지와 함께 로그인 페이지로 이동
            model.addAttribute("loginError", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "/member/login";
        }
    }

    @GetMapping("/member/join")
    public String join(){
        return "/member/join";
    }

    // 약관 동의
    @GetMapping("/member/signup")
    public String signup(@RequestParam("type") int type, Model model) {

        TermsDTO termsDTO = termsService.selectTerms();
        model.addAttribute("termsDTO", termsDTO);
        model.addAttribute("type", type);
        log.info("termsDTO : " + termsDTO);

        return "/member/signup";
    }

    @GetMapping("/member/signup_seller")
    public String signupseller(){
        return "/member/signup_seller";
    }

    @GetMapping("/member/register")
    public String register(){
        return "/member/register";
    }

    @PostMapping("/member/register")
    public String register(HttpServletRequest req, UserDTO userDTO){

        log.info(userDTO);

        userService.insertMemberUser(userDTO);
        log.info("insert 성공");

        return "redirect:/index?success=200";
    }

    @GetMapping("/member/register_seller")
    public String registerseller(){
        return "/member/register_seller";
    }

    @PostMapping("/member/register_seller")
    public String registerseller(HttpServletRequest req, UserDTO userDTO){

        log.info(userDTO);

        userService.insertSellerUser(userDTO);
        log.info("insert 성공");

        return "redirect:/index?success=200";
    }

    @GetMapping("/member/logout")
    public String logout(){
        return "redirect:/index";
    }

    // 중복확인 및 이메일 인증 코드 발송
    @ResponseBody
    @GetMapping("/member/{type}/{value}")
    public ResponseEntity<?> checkMember(HttpSession session,
                                         @PathVariable("type") String type,
                                         @PathVariable("value") String value) {

        log.info("type : " + type + ", value : " + value);

        int count = userService.selectCountUser(type, value);
        log.info("count : " + count);

        // 중복 없으면 이메일 인증코드 발송
        if(count == 0 && type.equals("email")){
            log.info("email : " + value);
            userService.sendEmailCode(session, value);
        }

        // JSON 생성
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", count);


        return ResponseEntity.ok().body(resultMap);
    }


    // 사업자판매회원 중복확인 및 이메일 인증 코드 발송
    @ResponseBody
    @GetMapping("/seller/{type}/{value}")
    public ResponseEntity<?> checkSeller(HttpSession session,
                                         @PathVariable("type") String type,
                                         @PathVariable("value") String value) {

        log.info("type : " + type + ", value : " + value);

        int count = userService.selectCountSellerUser(type, value);
        log.info("count : " + count);

        // 중복 없으면 이메일 인증코드 발송
        if(count == 0 && type.equals("email")){
            log.info("email : " + value);
            userService.sendEmailCode(session, value);
        }

        // JSON 생성
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", count);

        return ResponseEntity.ok().body(resultMap);
    }

    // 이메일 인증 코드 검사
    @ResponseBody
    @PostMapping("/member/email")
    public ResponseEntity<?> checkEmail(HttpSession session, @RequestBody Map<String, String> jsonData) {
        log.info("checkEmail code : " + jsonData);

        String receiveCode = userService.sendEmailCode(session, jsonData.get("email"));
        log.info("checkEmail receiveCode : " + receiveCode);

        // 세션에 저장된 인증 코드 가져오기
        String sessionCode = (String) session.getAttribute("code");

        Map<String, Object> resultMap = new HashMap<>();
        if (sessionCode != null && sessionCode.equals(receiveCode)) {
            resultMap.put("result", true);
        } else {
            resultMap.put("result", false);
        }
        resultMap.put("code", receiveCode);

        return ResponseEntity.ok().body(resultMap);
    }

    // member/seller 아이디찾기 유형 고르는 페이지
    @GetMapping("/member/find_id_select")
    public String findIdSelect() {

        return "/member/find_id_select";
    }

    // member/seller 비밀번호찾기 유형 고르는 페이지
    @GetMapping("/member/find_pass_select")
    public String findPassSelect() {

        return "/member/find_pass_select";
    }
    
    // 개인구매회원 아이디 찾기
    @GetMapping("/member/find_member_id")
    public String findId() {

        return "/member/find_member_id";
    }


    // 개인구매회원 아이디 찾기 결과
    @GetMapping("/member/find_id_result")
    public String findIdResult() {

        return "/member/find_id_result";
    }

    // 개인구매회원 아이디찾기 결과 post
    @PostMapping("/member/find_id_result")
    public String handleFindIdResult(UserDTO userDTO, RedirectAttributes redirectAttributes) {

        // 이름과 이메일로 아이디 조회
        String uid = userService.findIdByNameAndEmail(userDTO.getMemberInfo().getName(), userDTO.getMemberInfo().getEmail());
        log.info("uid : " + uid);

        if (uid != null) {
            UserDTO user = userService.selectUser(uid);
            log.info("user : " + user);
            redirectAttributes.addFlashAttribute("user", user); // 찾은 유저 정보를 뷰로 전달
            return "redirect:/member/find_id_result";
        } else {
            redirectAttributes.addFlashAttribute("error", "해당 정보로 등록된 아이디가 없습니다."); // Flash 속성으로 에러 메시지 전달
            return "redirect:/member/find_member_id";
        }
    }


    // 개인구매회원 비밀번호찾기 결과 post
    @PostMapping("/member/find_pass_result")
    public String handleFindPassResult(UserDTO userDTO, RedirectAttributes redirectAttributes) {

        // 아이디와 이메일로 아이디 조회
        String uid = userService.findAllByUidAndEmail(userDTO.getUid(), userDTO.getMemberInfo().getEmail());
        log.info("uid : " + uid);

        if (uid != null) {
            UserDTO user = userService.selectUser(uid);
            log.info("user : " + user);
            redirectAttributes.addFlashAttribute("user", user); // 찾은 유저 정보를 Flash Attribute로 전달
            return "redirect:/member/pass_change?uid=" + uid; // uid를 URL 파라미터로 추가
        } else {
            redirectAttributes.addFlashAttribute("error", "해당 정보로 등록된 회원이 없습니다."); // Flash 속성으로 에러 메시지 전달
            return "redirect:/member/find_member_pass";
        }
    }


    // 사업자판매회원 비밀번호찾기 결과 post
    @PostMapping("/member/find_seller_pass_result")
    public String handleFindSellerPassResult(UserDTO userDTO, RedirectAttributes redirectAttributes) {

        // 아이디와 사업자등록번호와 이메일로 아이디 조회
        String uid = userService.findAllByUidAndComNumberAndEmail(userDTO.getUid(), userDTO.getSellerInfo().getComNumber(), userDTO.getSellerInfo().getEmail());
        log.info("uid : " + uid);

        if (uid != null) {
            UserDTO user = userService.selectUser(uid);
            log.info("user : " + user);
            redirectAttributes.addFlashAttribute("user", user); // 찾은 유저 정보를 Flash Attribute로 전달
            return "redirect:/member/pass_change?uid=" + uid; // uid를 URL 파라미터로 추가
        } else {
            redirectAttributes.addFlashAttribute("error", "해당 정보로 등록된 회원이 없습니다."); // Flash 속성으로 에러 메시지 전달
            return "redirect:/member/find_seller_pass";
        }
    }
    
    // 개인구매회원 비밀번호 찾기
    @GetMapping("/member/find_member_pass")
    public String findMemberPass() {

        return "/member/find_member_pass";
    }

    // 사업자판매회원 비밀번호 찾기
    @GetMapping("/member/find_seller_pass")
    public String findSellerPass() {

        return "/member/find_seller_pass";
    }

    // 비밀번호 변경
    @GetMapping("/member/pass_change")
    public String passChange(@RequestParam("uid") String uid, Model model, UserDTO userDTO) {

        UserDTO user = userService.selectUser(uid);

        model.addAttribute("user", user);


        return "/member/pass_change";
    }

    // 비밀번호 변경
    @PostMapping("/member/pass_change")
    public String passChange(@RequestParam("uid") String uid,
                             @RequestParam("pass") String newPassword,
                             Model model, RedirectAttributes redirectAttributes) {
        log.info("uid : " + uid);
        log.info("newPassword : " + newPassword);
        boolean isUpdated = userService.updatePassword(uid, newPassword);

        if (isUpdated) {
            redirectAttributes.addFlashAttribute("success", "비밀번호가 성공적으로 변경되었습니다.");
            return "redirect:/member/login"; // 로그인 페이지로 리다이렉트
        } else {
            redirectAttributes.addFlashAttribute("error", "비밀번호 변경에 실패했습니다.");
            return "/member/pass_change";  // 비밀번호 변경 페이지로 다시 이동
        }
    }


    // 사업자판매회원 아이디 찾기
    @GetMapping("/member/find_seller_id")
    public String findSellerId() {

        return "/member/find_seller_id";
    }

    // 사업자판매회원 아이디 찾기
    @GetMapping("/member/find_seller_id_result")
    public String findSellerIdResult() {

        return "/member/find_seller_id_result";
    }

    // 사업자판매회원 아이디찾기 결과 post
    @PostMapping("/member/find_seller_id_result")
    public String handleFindSellerIdResult(UserDTO userDTO, RedirectAttributes redirectAttributes) {

        // 회사명과 사업자등록번호와 이메일로 아이디 조회
        String uid = userService.findIdByComNameAndComNumberAndEmail(userDTO.getSellerInfo().getComName(), userDTO.getSellerInfo().getComNumber(), userDTO.getSellerInfo().getEmail());
        log.info("uid : " + uid);

        if (uid != null) {
            UserDTO user = userService.selectUser(uid);
            log.info("user : " + user);
            redirectAttributes.addFlashAttribute("user", user); // 찾은 유저 정보를 뷰로 전달
            return "redirect:/member/find_seller_id_result";
        } else {
            redirectAttributes.addFlashAttribute("error", "해당 정보로 등록된 아이디가 없습니다."); // Flash 속성으로 에러 메시지 전달
            return "redirect:/member/find_seller_id";
        }
    }

}

