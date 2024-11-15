package com.lotte4.repository;

import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
/*
     날짜 : 2024/10/28
     이름 : 강은경
     내용 : UserRepository 생성

     수정이력
      - 2024/10/28 강은경 - 관리자 회원목록 기능 검색&페이징 메서드 추가
      - 2024/10/30 황수빈 - 포인트 조회 시 memberinfo로 uid를 찾아야함으로 메서드 추가
      - 2024/10/31 강은경 - 이름과 이메일로 아이디 조회하는 메서드 추가
      - 2024/10/31 황수빈 - 메서드 추가
      - 2024/11/01 강은경 - 검색조건 카테고리에 따라 검색되게 만들기 위해 메서드 추가
      - 2024/11/03 강은경 - 비밀번호 변경 및 정보에 따른 아이디 조회 하는 메서드 추가

*/
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByMemberInfo(MemberInfo memberInfo);
    List<User> findByUidContaining(String uid); // 2024/10/31(목) - 황수빈

    @Query("SELECT u.memberInfo.memberInfoId FROM User u WHERE u.uid = :uid")
    Integer findMemberInfoIdByUid(@Param("uid") String uid);

    // uid로 정보 조회
    Optional<User> findByUid(String uid);

    // 아이디 중복확인
    int countByUid(String uid);

    // role이 member인 회원 목록 select(관리자 회원&상점목록을 위함)
    Page<User> findByRole(String role, Pageable pageable);

    // uid 검색 
    Page<User> findByRoleAndUidContaining(String role, String uid, Pageable pageable);

    // 이름 검색
    Page<User> findByRoleAndMemberInfoNameContaining(String role, String name, Pageable pageable);

    // 이메일 검색
    Page<User> findByRoleAndMemberInfoEmailContaining(String role, String email, Pageable pageable);

    // 휴대폰 검색
    Page<User> findByRoleAndMemberInfoHpContaining(String role, String hp, Pageable pageable);

    // 이름과 이메일로 아이디 조회
    Optional<User> findByMemberInfo_nameAndMemberInfo_email(String memberInfo_name, String memberInfo_email);

    // 아이디와 이메일로 정보 조회
    Optional<User> findByUidAndMemberInfo_email(String uid, String memberInfo_email);

    // 비밀번호 변경
    @Modifying
    @Query("UPDATE User u SET u.pass = :pass WHERE u.uid = :uid")
    int updatePassword(@Param("uid") String uid, @Param("pass") String pass);

    // 회사명&사업자등록번호&이메일로 아이디 조회
    Optional<User> findBySellerInfo_ComNameAndSellerInfo_ComNumberAndSellerInfo_Email(String comName, String comNumber, String email);

    // 아이디&사업자등록번호&이메일로 정보 조회
    Optional<User> findByUidAndAndSellerInfo_ComNumberAndSellerInfo_Email(String uid, String comNumber, String email);

    // 상호명 검색
    Page<User> findByRoleAndSellerInfoComNameContaining(String role, String comName, Pageable pageable);

    // 대표자 검색
    Page<User> findByRoleAndSellerInfoCeoContaining(String role, String ceo, Pageable pageable);

    // 사업자등록번호 검색
    Page<User> findByRoleAndSellerInfoBizNumberContaining(String role, String bizNumber, Pageable pageable);

    // 연락처 검색
    Page<User> findByRoleAndSellerInfoHpContaining(String role, String hp, Pageable pageable);

    @Query("SELECT COUNT(b) FROM User b WHERE DATE(b.createdAt) = :today")
    int findAllByDay(@Param("today") LocalDate day);

}
