package com.lotte4.repository;

import com.lotte4.entity.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberInfoRepository extends JpaRepository<MemberInfo, Integer> {

    // 중복확인
    int countByEmail(String email);
    int countByHp(String hp);

    // memberInfoId로 정보 조회
    Optional<MemberInfo> findByMemberInfoId(int memberInfoId);

    // 여러 사용자의 등급을 업데이트하기 위한 메서드 정의(등급 선택수정)
    @Modifying
    @Query("UPDATE MemberInfo u SET u.grade = :grade WHERE u.memberInfoId = :memberInfoId")
    void updateGradeByMemberInfoId(@Param("memberInfoId") Integer memberInfoId, @Param("grade") String grade);




}
