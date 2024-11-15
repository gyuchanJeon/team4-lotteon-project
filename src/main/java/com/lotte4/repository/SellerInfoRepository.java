package com.lotte4.repository;

import com.lotte4.entity.SellerInfo;
import com.lotte4.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/*
     날짜 : 2024/11/03
     이름 : 강은경
     내용 : SellerInfoRepository 생성

     수정이력
      - 2024/11/03 강은경 - 이메일 중복확인 메서드추가
*/
@Repository
public interface SellerInfoRepository extends JpaRepository<SellerInfo, Integer> {

    // 중복확인
    int countByEmail(String email);



}
