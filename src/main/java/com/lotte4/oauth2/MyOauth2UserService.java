package com.lotte4.oauth2;

import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.Point;
import com.lotte4.entity.User;
import com.lotte4.repository.PointRepository;
import com.lotte4.repository.UserRepository;
import com.lotte4.security.MyUserDetails;
import com.lotte4.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class MyOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final UserService userService;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {

            log.info("loadUser...1 : " + userRequest);

            String accessToken = userRequest.getAccessToken().getTokenValue();
            log.info("loadUser...2 : " + accessToken);

            String provider = userRequest.getClientRegistration().getRegistrationId();
            log.info("loadUser...3 : " + provider);

            OAuth2User oAuth2User = super.loadUser(userRequest);
            log.info("loadUser...4 : " + oAuth2User);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.info("loadUser...5 : " + attributes);

            // 사용자 확인 및 회원가입 처리
            String email = (String) attributes.get("email");
            String uid = "GOOGLE_" + email.split("@")[0];
            String name = attributes.get("given_name").toString();


            Optional<User> optUser = userRepository.findByUid(uid);

            if(optUser.isPresent()) {
                // 회원 존재하면 시큐리티 인증처리(로그인)
                User user = optUser.get();

                // 로그인 날짜 업데이트
                userService.updateLastLoginDate(user.getUid());

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            }else {
                // 신규 회원일 경우 User 및 MemberInfo 생성
                MemberInfo memberInfo = MemberInfo.builder()
                        .name(name)
                        .email(email)
                        .status("정상") // 기본값 설정
                        .grade("FAMILY")  // 기본값 설정
                        .point(5000)
                        .build();

                User user = User.builder()
                                .uid(uid)
                                .memberInfo(memberInfo)
                                .role("member") // 기본 역할 설정
                                .build();

                userRepository.save(user);

                // 로그인 날짜 업데이트
                userService.updateLastLoginDate(user.getUid());

                pointRepository.save( Point.builder()
                        .pointName("회원가입 축하 포인트")
                        .memberInfo(memberInfo)
                        .type("적립")
                        .point(5000)
                        .presentPoint(5000)
                        .build() );

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            }
        }else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            log.info("Kakao 로그인...1 : " + userRequest);

            String accessToken = userRequest.getAccessToken().getTokenValue();
            log.info("Kakao 로그인...2 : " + accessToken);

            OAuth2User oAuth2User = super.loadUser(userRequest);
            log.info("Kakao 로그인...3 : " + oAuth2User);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.info("Kakao 로그인...4 : " + attributes);

            // 카카오 응답에서 사용자 정보 가져오기
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            // 이메일 정보 추출
            String email = (String) kakaoAccount.get("email");

            // 닉네임 또는 이름 정보 추출
            String name = (String) profile.get("nickname");

            // 이메일에서 사용자 uid 추출
            String uid = email != null ? "KAKAO_" + email.split("@")[0] : null;

            // 결과 출력
            System.out.println("이메일: " + email);
            System.out.println("이름: " + name);
            System.out.println("사용자 ID: " + uid);


            Optional<User> optUser = userRepository.findByUid(uid);

            if (optUser.isPresent()) {
                // 기존 회원인 경우 로그인 처리
                User user = optUser.get();
                userService.updateLastLoginDate(user.getUid());

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            } else {
                // 신규 회원일 경우 회원가입 처리
                MemberInfo memberInfo = MemberInfo.builder()
                        .name(name)
                        .email(email)
                        .status("정상")
                        .grade("FAMILY")
                        .point(5000)
                        .build();

                User user = User.builder()
                        .uid(uid)
                        .memberInfo(memberInfo)
                        .role("member")
                        .build();

                userRepository.save(user);

                // 로그인 날짜 업데이트
                userService.updateLastLoginDate(user.getUid());

                pointRepository.save(Point.builder()
                        .pointName("회원가입 축하 포인트")
                        .memberInfo(memberInfo)
                        .type("적립")
                        .point(5000)
                        .presentPoint(5000)
                        .build());

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            }
        }else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            log.info("Naver 로그인...1 : " + userRequest);

            String accessToken = userRequest.getAccessToken().getTokenValue();
            log.info("Naver 로그인...2 : " + accessToken);

            OAuth2User oAuth2User = super.loadUser(userRequest);
            log.info("Naver 로그인...3 : " + oAuth2User);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.info("Naver 로그인...4 : " + attributes);

            // 카카오 응답에서 사용자 정보 가져오기
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");

            // 이메일 정보 추출
            String email = (String) response.get("email");

            // 닉네임 또는 이름 정보 추출
            String name = (String) response.get("name");

            // 이메일에서 사용자 uid 추출
            String uid = email != null ? "NAVER_" + email.split("@")[0] : null;

            // 결과 출력
            System.out.println("이메일: " + email);
            System.out.println("이름: " + name);
            System.out.println("사용자 ID: " + uid);


            Optional<User> optUser = userRepository.findByUid(uid);

            if (optUser.isPresent()) {
                // 기존 회원인 경우 로그인 처리
                User user = optUser.get();
                userService.updateLastLoginDate(user.getUid());

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            } else {
                // 신규 회원일 경우 회원가입 처리
                MemberInfo memberInfo = MemberInfo.builder()
                        .name(name)
                        .email(email)
                        .status("정상")
                        .grade("FAMILY")
                        .point(5000)
                        .build();

                User user = User.builder()
                        .uid(uid)
                        .memberInfo(memberInfo)
                        .role("member")
                        .build();

                userRepository.save(user);

                // 로그인 날짜 업데이트
                userService.updateLastLoginDate(user.getUid());

                pointRepository.save(Point.builder()
                        .pointName("회원가입 축하 포인트")
                        .memberInfo(memberInfo)
                        .type("적립")
                        .point(5000)
                        .presentPoint(5000)
                        .build());

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            }
        } else {
            return null;
        }
    }
}