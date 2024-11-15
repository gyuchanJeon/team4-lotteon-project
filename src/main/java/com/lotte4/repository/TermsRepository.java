package com.lotte4.repository;

import com.lotte4.entity.Terms;
import com.lotte4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/*
     날짜 : 2024/10/28
     이름 : 강은경
     내용 : TermsRepository 생성

     수정이력
      - 2024/10/28 강은경 - terms select&update 메서드 추가
*/
@Repository
public interface TermsRepository extends JpaRepository<Terms, Integer> {

    Terms findByTermsId(int termsId);


    // 구매회원 약관 update
    @Modifying
    @Query("UPDATE Terms i SET i.term = :term WHERE i.termsId = 1")
    int updateTerm(@Param("term") String term);


    // 판매회원 약관 update
    @Modifying
    @Query("UPDATE Terms i SET i.tax = :tax WHERE i.termsId = 1")
    int updateTax(@Param("tax") String tax);

    // 전자금융거래 약관 update
    @Modifying
    @Query("UPDATE Terms i SET i.finance = :finance WHERE i.termsId = 1")
    int updateFinance(@Param("finance") String finance);

    // 위치정보 이용약관 update
    @Modifying
    @Query("UPDATE Terms i SET i.location = :location WHERE i.termsId = 1")
    int updateLocation(@Param("location") String location);

    // 개인정보처리방침 update
    @Modifying
    @Query("UPDATE Terms i SET i.privacy = :privacy WHERE i.termsId = 1")
    int updatePrivacy(@Param("privacy") String privacy);





}
