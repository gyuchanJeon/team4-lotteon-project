package com.lotte4.repository;

import com.lotte4.entity.VisitorCount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/*

    2024.11.06 강중원 - 방문자 통계용 서비스 생성

 */

public interface VisitorCountRepository extends MongoRepository<VisitorCount, String> {
    public Optional<VisitorCount> findByDate(String date);

}
