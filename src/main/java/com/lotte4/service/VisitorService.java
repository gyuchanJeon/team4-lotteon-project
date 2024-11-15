package com.lotte4.service;

import com.lotte4.entity.VisitorCount;
import com.lotte4.repository.VisitorCountRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/*

    2024.11.06 강중원 - 방문자 통계용 서비스 생성

 */

@Log4j2
@RequiredArgsConstructor
@Service
public class VisitorService {
    private final RedisTemplate redisTemplate;
    private final VisitorCountRepository visitorCountRepository;

    public void registerVisit(HttpServletRequest request) {
        String visitorId = getVisitorId(request);
        String dateKey = LocalDate.now().toString();
        String visitorSetKey = "daily_visitors:" + dateKey;
        String visitorCountKey = "daily_visitor_count:" + dateKey;

        // Redis Set에 새 방문자 추가 및 카운터 증가
        boolean isNewVisitor = redisTemplate.opsForSet().add(visitorSetKey, visitorId) == 1;
        if (isNewVisitor) {
            redisTemplate.opsForValue().increment(visitorCountKey);
        }
    }

    private String getVisitorId(HttpServletRequest request) {
        String sessionId = request.getSession(true).getId(); // 세션 ID 사용
        return sessionId; // 세션 ID 기준
    }

    // 매일 자정에 방문자 수를 MongoDB에 저장하는 작업
    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정 00:00에 실행
    public void saveDailyVisitorCount() {
        String dateKey = LocalDate.now().minusDays(1).toString(); // 자정 기준 어제 날짜
        String visitorCountKey = "daily_visitor_count:" + dateKey;
        String visitorSetKey = "daily_visitors:" + dateKey;

        // Redis에서 방문자 수 가져오기
        Integer count = (Integer) redisTemplate.opsForValue().get(visitorCountKey);
        if (count != null) {
            // MongoDB에 방문자 수 저장
            VisitorCount visitorCount = new VisitorCount(dateKey, count);
            visitorCountRepository.save(visitorCount);
        }
        log.info("count : " + count);

        // Redis에서 해당 키들 삭제 (하루 방문자 데이터 초기화)
        redisTemplate.delete(visitorCountKey);
        redisTemplate.delete(visitorSetKey);
    }

    //오늘(현재) 방문자수 가져오는 메서드 - Redis
    public Integer getVisitorCountToday() {
        String dateKey = LocalDate.now().toString(); // 오늘 날짜
        String visitorCountKey = "daily_visitor_count:" + dateKey;

        // Redis에서 방문자 수 가져오기
        return (Integer) redisTemplate.opsForValue().get(visitorCountKey);
    }

    //어제 방문자수 가져오는 메서드
    public Integer getVisitorCountYesterday() {
        String dateKey = LocalDate.now().minusDays(1).toString(); // 자정 기준 어제 날짜
        Optional<VisitorCount> yesterdayCount = visitorCountRepository.findByDate(dateKey);
        if (yesterdayCount.isPresent()) {
            VisitorCount visitorCount = yesterdayCount.get();
            return visitorCount.getCount();
        }
        return 0;
    }
    //전체 방문자수
    public Integer getTotalVisitorCount() {
        int total = 0;
        List<VisitorCount> visitorCountList = visitorCountRepository.findAll();
        for (VisitorCount visitorCount : visitorCountList) {
            total += visitorCount.getCount();
        }
        return total;
    }
}
