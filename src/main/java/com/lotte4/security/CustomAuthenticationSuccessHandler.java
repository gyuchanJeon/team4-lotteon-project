
package com.lotte4.security;

import com.lotte4.entity.User;
import com.lotte4.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler  {


    private final UserService userService;

    // 순환 참조 문제 해결하기 위해서 @Lazy 사용 > 일부 빈을 지연 초기화 
    public CustomAuthenticationSuccessHandler(@Lazy UserService userService) {
        this.userService = userService;
    }

    // 로그인 인증이 성공했을 때 호출되는 메서드 - 2024-11-07 강은경
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 로그인 성공 시, remember-me 파라미터 체크
        String rememberMe = request.getParameter("remember-me");

        if ("on".equals(rememberMe)) {
            log.info("자동 로그인 활성화됨");
        } else {
            log.info("자동 로그인 비활성화됨");
        }

        // 역할에 따라 리다이렉트할 URL 설정
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .orElse("ROLE_member"); // 기본값 설정

        // 최근 로그인 날짜 업데이트
        String username = authentication.getName();

        Optional<User> userOptional = Optional.ofNullable(userService.findByUid(username));

        // user가 존재하고, 상태가 "탈퇴"인 경우
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // user의 MemberInfo가 null이 아니고, 상태가 "탈퇴"인 경우
            if (user.getMemberInfo() != null && "탈퇴".equals(user.getMemberInfo().getStatus())) {
                log.warn("로그인 실패: 사용자 " + username + "는 탈퇴 상태입니다.");
                response.sendRedirect("/lotteon/member/login?success=401"); // 탈퇴된 사용자는 로그인 불가
                return; // 이후 로직을 실행하지 않도록 종료
            }
        }


        userService.updateLastLoginDate(username);

        // 역할에 따라 리다이렉트할 URL 결정
        String redirectUrl;
        if (role.equals("ROLE_ADMIN") || role.equals("ROLE_seller")) {
            redirectUrl = "/lotteon/admin/index"; // 관리자로 로그인 시 리다이렉트
        } else {
            redirectUrl = "/lotteon/index"; // 일반 사용자 로그인 시 리다이렉트
        }

        response.sendRedirect(redirectUrl);
    }


}
