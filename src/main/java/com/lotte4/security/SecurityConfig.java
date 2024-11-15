package com.lotte4.security;

import com.lotte4.oauth2.MyOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
//    private final MyOauth2UserService myOauth2UserService;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final MyUserDetailsService myUserDetailsService;



    @Bean
    public SecurityFilterChain configure(HttpSecurity http, MyOauth2UserService myOauth2UserService) throws Exception {
        
        // 로그인 설정
        http.formLogin(login -> login
                .loginPage("/member/login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureUrl("/member/login?success=100")
                .usernameParameter("uid")
                .passwordParameter("pass"));

        // 자동로그인 설정
        http.rememberMe(rememberMe -> rememberMe
                .key("lotteonlogincookiekey")
                .tokenValiditySeconds(60 * 60 * 24) // 24시간을 초단위로 설정
                .rememberMeParameter("remember-me")
                .userDetailsService(myUserDetailsService));

        // 로그아웃 설정
        http.logout(logout -> logout
                .invalidateHttpSession(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .deleteCookies("JSESSIONID", "remember-me"));  // 로그아웃시 쿠키 삭제

        // 인가 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_seller")
                .requestMatchers("/my/**").hasAnyAuthority("ROLE_member")
                .anyRequest().permitAll());



        // 기타 보안 설정
        http.oauth2Login(login->login
                .loginPage("/member/login")
                .defaultSuccessUrl("/index")
                .userInfoEndpoint(endpoint->endpoint
                        .userService(myOauth2UserService)
                )
        );

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }




}
