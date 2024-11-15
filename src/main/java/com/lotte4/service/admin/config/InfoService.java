package com.lotte4.service.admin.config;

import com.lotte4.dto.admin.config.InfoDTO;
import com.lotte4.entity.Info;
import com.lotte4.repository.admin.config.InfoRepository;
import com.lotte4.service.CachingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Log4j2
public class InfoService {

    private final CachingService cachingService;    //캐싱을 위한 서비스 - 강중원 10.31
    private final InfoRepository infoRepository;
    private final ModelMapper modelMapper;

    public Info selectInfo() {
        return infoRepository.findById(1).orElse(new Info());
    }

    //캐싱 기능추가 10.31 강중원
    public InfoDTO selectInfoDTO() {
        return cachingService.selectInfoDTO();
    }

    public InfoDTO updateInfoTitleAndSubtitle(InfoDTO infoDTO) {
        return cachingService.updateInfoTitleAndSubtitle(infoDTO);
    }

    public InfoDTO updateCompanyInfo(InfoDTO infoDto) {
        return cachingService.updateCompanyInfo(infoDto);
    }

    public InfoDTO updateCompanyCs(InfoDTO infoDto) {
        return cachingService.updateCompanyCs(infoDto);
    }

    public InfoDTO updateCopyright(InfoDTO infoDto) {
        return cachingService.updateCopyright(infoDto);
    }


    public InfoDTO uploadLogos(MultipartFile headerLogo, MultipartFile footerLogo, MultipartFile favicon) throws IOException {
        return cachingService.uploadLogos(headerLogo, footerLogo, favicon);
    }
}

