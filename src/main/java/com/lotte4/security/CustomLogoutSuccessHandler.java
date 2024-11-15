package com.lotte4.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 파라미터 읽기 (예: `type`이라는 파라미터)
        String type = request.getParameter("type");

        // 파라미터 값에 따라 다른 경로로 리다이렉트
        if ("delete".equals(type)) {
            response.sendRedirect("/lotteon/member/login?success=400");
        } else if ("change".equals(type)){
            // 기본 경로로 리다이렉트
            response.sendRedirect("/lotteon/member/login?success=102");
        }else {
            // 기본 경로로 리다이렉트
            response.sendRedirect("/lotteon/member/login?success=101");
        }
    }
}
