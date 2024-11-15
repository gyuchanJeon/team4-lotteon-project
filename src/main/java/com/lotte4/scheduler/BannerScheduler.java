package com.lotte4.scheduler;

import com.lotte4.dto.BannerDTO;
import com.lotte4.service.admin.config.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class BannerScheduler {

    private final BannerService bannerService;

    //1분마다 실행
    @Scheduled(cron = "0 0/10 * * * *")
    public void bannerCheck() {
        List<BannerDTO> bannerList = bannerService.getAllBanners();
        for (BannerDTO bannerDTO : bannerList) {
            //활성화 된 banner만
            if(bannerDTO.getState() == 1) {
                Date eDate = bannerDTO.getEDate();
                String time = bannerDTO.getETime();

                //LocalDateTime으로 변환
                LocalDateTime eDay = new java.sql.Timestamp(eDate.getTime()).toLocalDateTime();
                eDay = eDay.plusHours(Long.parseLong(time.substring(0,2)));
                eDay = eDay.plusMinutes(Long.parseLong(time.substring(3,5)));
                //현재 시간과 비교
                boolean expire = eDay.isBefore(LocalDateTime.now());
                //현재시간보다 지났다면 AND 활성화 상태이면
                if (expire && bannerDTO.getState() != 0) {
                    //만료
                    bannerService.updateBannerState(bannerDTO, 0);
                }

            }

        }
    }
}
