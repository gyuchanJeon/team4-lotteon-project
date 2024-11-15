package com.lotte4.service.admin.config;

import com.lotte4.dto.BannerDTO;
import com.lotte4.entity.Banner;
import com.lotte4.repository.admin.config.BannerRepository;
import com.lotte4.service.CachingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class BannerService {
    private final ModelMapper modelMapper;
    private final BannerRepository bannerRepository;
    private final CachingService cachingService;

    public BannerDTO getBanner(int bannerId) {
        Optional<Banner> banner = bannerRepository.findById(bannerId);
        return banner.map(value -> modelMapper.map(value, BannerDTO.class)).orElse(null);
    }

    public List<BannerDTO> getAllBanners(){
        return cachingService.getAllBanners();
    }

    public List<BannerDTO> getBannersByLocation(String location){
        List<Banner> banners = bannerRepository.findByLocation(location);
        List<BannerDTO> bannerDTOs = new ArrayList<>();

        for(Banner banner : banners){
            bannerDTOs.add(modelMapper.map(banner, BannerDTO.class));
        }
        return bannerDTOs;
    }




    public List<BannerDTO> getAllBannersWithLocation(){
        Random random = new Random();
        List<List<BannerDTO>> findResult = cachingService.getAllBannersWithLocationPre();

        //메인 슬라이드 전부 넣기
        List<BannerDTO> bannerDTOs = findResult.get(0);
        //랜덤하게 섞기
        Collections.shuffle(bannerDTOs);

        //랜덤으로 하나 정해서 리스트에 삽입
        for (int count = 1; count < findResult.size(); count++) {
            List<BannerDTO> bannerDTOList = findResult.get(count);
            if (!bannerDTOList.isEmpty()){
                bannerDTOs.add(bannerDTOList.get(random.nextInt(bannerDTOList.size())));
            }
        }
        return bannerDTOs;
    }


    public String insertBanner(BannerDTO bannerDTO, MultipartFile bannerImg) throws IOException {

        BannerDTO resultBannerDTO = cachingService.insertBanner(bannerDTO, bannerImg);

        cachingService.clearAllEnableBanners();
        if(resultBannerDTO.getName() == null){
            return "fail";
        }
        return "success";
    }

    public void deleteBanner(int bannerId){
        cachingService.deleteBanner(bannerId);

        cachingService.clearAllEnableBanners();
    }

    public void updateBannerState(BannerDTO bannerDTO, int state){
        cachingService.updateBannerState(bannerDTO, state);
        cachingService.clearAllEnableBanners();

    }

    public boolean expireBannerCheck(BannerDTO bannerDTO){
        Optional<Banner> bannerOpt = bannerRepository.findById(bannerDTO.getBannerId());
        if(bannerOpt.isPresent()) {
            Banner banner = bannerOpt.get();
            Date eDate = banner.getEDate();
            String time = banner.getETime();

            //LocalDateTime으로 변환
            LocalDateTime eDay = new java.sql.Timestamp(eDate.getTime()).toLocalDateTime();
            eDay = eDay.plusHours(Long.parseLong(time.substring(0, 2)));
            eDay = eDay.plusMinutes(Long.parseLong(time.substring(3, 5)));
            //현재 시간과 비교
            boolean expire = eDay.isBefore(LocalDateTime.now());
            return expire;
        }
        return false;
    }

    public void clearBannerCache(){
        cachingService.clearAllBannersWithLocation();
        cachingService.clearAllEnableBanners();
    }
}
