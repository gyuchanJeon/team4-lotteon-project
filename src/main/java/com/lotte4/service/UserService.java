package com.lotte4.service;

import com.lotte4.dto.CartDTO;
import com.lotte4.dto.MemberInfoDTO;
import com.lotte4.dto.UserDTO;
import com.lotte4.dto.UserPointCouponDTO;
import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.Point;
import com.lotte4.entity.SellerInfo;
import com.lotte4.entity.User;
import com.lotte4.repository.MemberInfoRepository;
import com.lotte4.repository.PointRepository;
import com.lotte4.repository.SellerInfoRepository;
import com.lotte4.repository.UserRepository;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
/*
     날짜 : 2024/10/28
     이름 : 강은경
     내용 : UserService 생성

     수정이력
      - 2024/10/28 강은경 - 관리자 회원목록 기능 검색&페이징 메서드 추가
      - 2024/10/28 강은경 - uid로 사용자 조회 메서드 추가
      - 2024/10/28 강은경 - 이름과 이메일로 아이디 조회하는 메서드 추가
      - 2024/11/03 강은경 - 정보에 따른 아이디 조회하는 메서드 추가
      - 2024-11-07 조수빈 - 사용자 find멤버 infoID조회 메서드 추가
*/
@Log4j2
@RequiredArgsConstructor
@Service
public class UserService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final MemberInfoService memberInfoService;
    private final SellerInfoService sellerInfoService;
    private final ModelMapper modelMapper;
    private final JavaMailSender javaMailSender;
    private final MemberInfoRepository memberInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final SellerInfoRepository sellerInfoRepository;

    @Value("${spring.mail.username}")
    private String sender;

    // 일반회원 회원가입
    public void insertMemberUser(UserDTO userDTO) {

        // 비밀번호 인코딩
        String encoded = passwordEncoder.encode(userDTO.getPass());
        userDTO.setPass(encoded);

        // MemberInfo 저장
        MemberInfo memberInfo = memberInfoService.insertMemberInfo(userDTO.getMemberInfo());

        // User 저장
        User user = User.builder()
                .uid(userDTO.getUid())
                .pass(userDTO.getPass())
                .role(userDTO.getRole())
                .createdAt(userDTO.getCreatedAt())
                .leaveDate(userDTO.getLeaveDate())
                .memberInfo(memberInfo)
                .build();

        userRepository.save(user);

        pointRepository.save( Point.builder()
                        .pointName("회원가입 축하 포인트")
                        .memberInfo(memberInfo)
                        .type("적립")
                        .point(5000)
                        .presentPoint(5000)
                        .build() );


        log.info("memberuser" + user);

    }

    // 판매자 회원가입
    public void insertSellerUser(UserDTO userDTO) {

        // 비밀번호 인코딩
        String encoded = passwordEncoder.encode(userDTO.getPass());
        userDTO.setPass(encoded);

        // SellerInfo 저장
        SellerInfo sellerInfo = sellerInfoService.insertSellerInfo(userDTO.getSellerInfo());

        // User 저장
        User user = User.builder()
                .uid(userDTO.getUid())
                .pass(userDTO.getPass())
                .role(userDTO.getRole())
                .createdAt(userDTO.getCreatedAt())
                .leaveDate(userDTO.getLeaveDate())
                .sellerInfo(sellerInfo)
                .build();

        userRepository.save(user);

        log.info("selleruser" + user);

    }

    public UserDTO login(String uid, String pass) {
        // UserRepository를 사용하여 userId로 사용자 정보 조회
        Optional<User> userOptional = userRepository.findByUid(uid);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 비밀번호 검증 (PasswordEncoder를 사용하여 매칭 확인)
            if (passwordEncoder.matches(pass, user.getPass())) {
                log.info("로그인 성공 - uid: " + uid);
                // User 엔티티를 UserDTO로 매핑하여 반환
                return modelMapper.map(user, UserDTO.class);
            } else {
                log.warn("비밀번호가 일치하지 않습니다 - uid: " + uid);
            }
        } else {
            log.warn("존재하지 않는 사용자 - uid: " + uid);
        }

        return null; // 로그인 실패 시 null 반환
    }


    // 사용자 조회
    public UserDTO selectUser(String uid) {
        return userRepository.findByUid(uid)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElse(null);
    }

    // 사용자 중복 체크(개인구매회원)
    public int selectCountUser(String type, String value){
        int count = 0;

        if(type.equals("uid")){
            count = userRepository.countByUid(value);
        } else if(type.equals("email")){
            count = memberInfoRepository.countByEmail(value);
        } else if (type.equals("hp")) {
            count = memberInfoRepository.countByHp(value);
        }
        return count;
    }

    // 사용자 중복 체크(사업자판매회원)
    public int selectCountSellerUser(String type, String value){
        int count = 0;

        if(type.equals("uid")){
            count = userRepository.countByUid(value);
        } else if(type.equals("email")){
            count = sellerInfoRepository.countByEmail(value);
        } else if (type.equals("hp")) {
            count = memberInfoRepository.countByHp(value);
        }
        return count;
    }

    // 이메일 인증 코드 발송
    public String sendEmailCode(HttpSession session, String receiver) {

        log.info("sender : " + sender);

        // MimeMessage 생성
        MimeMessage message = javaMailSender.createMimeMessage();

        // 인증코드 생성 후 세션 저장
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        session.setAttribute("code", String.valueOf(code));
        log.info("code : " + code);

        String title = "lotteOn 인증코드 입니다.";
        String content = "<h1>인증코드는 " + code + "입니다.</h1>";

        try {
            message.setFrom(new InternetAddress(sender, "lotteOn", "UTF-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            message.setSubject(title);
            message.setContent(content, "text/html;charset=UTF-8");

            // 메일 발송
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("sendEmailCode : " + e.getMessage());
        }

        return code + "";
    }

    // 검색조건에 따른 회원 목록 출력
    public Page<UserDTO> selectUserListByMember(String role, int page, int size, String keyword, String searchCategory) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        // 검색 키워드가 있을 때 searchCategory에 따라 조건을 나눔
        if (keyword != null && !keyword.isEmpty()) {
            switch (searchCategory) {
                case "uid":
                    userPage = userRepository.findByRoleAndUidContaining(role, keyword, pageable);
                    break;
                case "name":
                    userPage = userRepository.findByRoleAndMemberInfoNameContaining(role, keyword, pageable);
                    break;
                case "email":
                    userPage = userRepository.findByRoleAndMemberInfoEmailContaining(role, keyword, pageable);
                    break;
                case "hp":
                    userPage = userRepository.findByRoleAndMemberInfoHpContaining(role, keyword, pageable);
                    break;
                default:
                    // 기본적으로 모든 필드를 포함하는 검색
                    userPage = userRepository.findByRole(
                            role, pageable);
                    break;
            }
        } else {
            // 키워드가 없을 경우 기본값으로 모든 사용자 가져오기
            userPage = userRepository.findByRole(role, pageable);
        }

        // User 엔티티를 UserDTO로 변환
        return userPage.map(user -> modelMapper.map(user, UserDTO.class));
    }


    // uid로 사용자 조회
    public User findByUid(String uid) {
        // UserRepository를 사용하여 uid로 사용자 정보를 조회
        Optional<User> userOptional = userRepository.findByUid(uid);

        if(userOptional.isPresent()) {
            return userOptional.get();
        } else {
            log.warn("사용자를 찾을 수 없습니다 - uid: " + uid);
            return null; // 사용자가 없을 경우 null 반환
        }

    }

    // 이름과 이메일로 아이디 조회
    public String findIdByNameAndEmail(String name, String email) {

        Optional<User> userOptional = userRepository.findByMemberInfo_nameAndMemberInfo_email(name, email);

        if(userOptional.isPresent()) {
            log.info("아이디 찾기 성공 - name: " + name + ", email: " + email);
            return userOptional.get().getUid();
        } else {
            return null;
        }
    }

    // 아이디와 이메일로 정보 조회
    public String findAllByUidAndEmail(String uid, String email) {

        Optional<User> userOptional = userRepository.findByUidAndMemberInfo_email(uid, email);

        if(userOptional.isPresent()) {
            log.info("정보 찾기 성공 - uid: " + uid + ", email: " + email);
            return userOptional.get().getUid();
        } else {
            return null;
        }
    }

    // 아이디와 사업자등록번호와 이메일로 정보 조회
    public String findAllByUidAndComNumberAndEmail(String uid, String comNumber,String email) {

        Optional<User> userOptional = userRepository.findByUidAndAndSellerInfo_ComNumberAndSellerInfo_Email(uid, comNumber, email);

        if(userOptional.isPresent()) {
            log.info("정보 찾기 성공 - uid: " + uid + ", comNumber: " + comNumber+ ", email: " + email);
            return userOptional.get().getUid();
        } else {
            return null;
        }
    }

    // 비밀번호 변경
    @Transactional
    public boolean updatePassword(String uid, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        int result = userRepository.updatePassword(uid, encodedPassword);
        return result > 0;  // 업데이트가 성공하면 true 반환
    }

    // 회사명과 사업자등록번호와 이메일로 아이디 조회
    public String findIdByComNameAndComNumberAndEmail(String comName, String comNumber, String email) {

        Optional<User> userOptional = userRepository.findBySellerInfo_ComNameAndSellerInfo_ComNumberAndSellerInfo_Email(comName, comNumber, email);

        if(userOptional.isPresent()) {
            log.info("아이디 찾기 성공 - comName: " + comName + ", comNumber: " + comNumber + ", email: " + email);
            return userOptional.get().getUid();
        } else {
            return null;
        }
    }

    // 최근 로그인 날짜 업데이트 - 2024-11-07 강은경
    @Transactional
    public void updateLastLoginDate(String username) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = LocalDateTime.now().format(formatter);

        Optional<User> userOptional = userRepository.findByUid(username);

        // User 객체가 존재하는지 확인 후 업데이트 수행
        userOptional.ifPresent(user -> {
            MemberInfo memberInfo = user.getMemberInfo();  // User의 MemberInfo 객체 가져오기
            if (memberInfo != null) {
                memberInfo.setLastLoginAt(formattedDate); // 최근 로그인 날짜 설정
            }
            userRepository.save(user); // 변경 사항 저장
        });
    }

    public Integer getMemberInfoIdByUid(String uid) {
        return userRepository.findMemberInfoIdByUid(uid);
    }

    public Optional<User> getUserByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    // 검색조건에 따른 상점 목록 출력
    public Page<UserDTO> selectUserListBySeller(String role, int page, int size, String keyword, String searchCategory) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        // 검색 키워드가 있을 때 searchCategory에 따라 조건을 나눔
        if (keyword != null && !keyword.isEmpty()) {
            switch (searchCategory) {
                case "comName":
                    userPage = userRepository.findByRoleAndSellerInfoComNameContaining(role, keyword, pageable);
                    break;
                case "ceo":
                    userPage = userRepository.findByRoleAndSellerInfoCeoContaining(role, keyword, pageable);
                    break;
                case "bizNumber":
                    userPage = userRepository.findByRoleAndSellerInfoBizNumberContaining(role, keyword, pageable);
                    break;
                case "hp":
                    userPage = userRepository.findByRoleAndSellerInfoHpContaining(role, keyword, pageable);
                    break;
                default:
                    // 기본적으로 모든 필드를 포함하는 검색
                    userPage = userRepository.findByRole(
                            role, pageable);
                    break;
            }
        } else {
            // 키워드가 없을 경우 기본값으로 모든 사용자 가져오기
            userPage = userRepository.findByRole(role, pageable);
        }

        // User 엔티티를 UserDTO로 변환
        return userPage.map(user -> modelMapper.map(user, UserDTO.class));
    }

    // 탈퇴 처리 메서드
    public boolean quitUser(String uid) {
        // uid로 사용자를 찾음
        Optional<User> userOptional = userRepository.findByUid(uid);

        // User 객체가 존재하는지 확인 후 업데이트 수행
        if (userOptional.isPresent()) {
            User user = userOptional.get();  // User 객체 가져오기
            MemberInfo memberInfo = user.getMemberInfo();  // User의 MemberInfo 객체 가져오기

            if (memberInfo != null) {
                memberInfo.setStatus("탈퇴");  // 상태를 '탈퇴'로 설정
                userRepository.save(user);  // 변경 사항 저장

                return true;  // 성공적으로 탈퇴 처리됨
            }
        }

        return false;  // 해당 사용자가 존재하지 않거나 상태 업데이트 실패
    }

    public int findAllByDay(LocalDate date) {
        return userRepository.findAllByDay(date);
    }


}
