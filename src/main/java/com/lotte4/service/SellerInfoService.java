package com.lotte4.service;

import com.lotte4.dto.SellerInfoDTO;
import com.lotte4.entity.SellerInfo;
import com.lotte4.repository.SellerInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class SellerInfoService {

    private final SellerInfoRepository sellerInfoRepository;
    private final ModelMapper modelMapper;

    // 판매자 정보 저장
    public SellerInfo insertSellerInfo(SellerInfoDTO sellerInfoDTO) {

        log.info("sellerInfoDTO: " + sellerInfoDTO);
        SellerInfo sellerInfo = sellerInfoDTO.toEntity();
        return sellerInfoRepository.save(sellerInfo);

    }

    // 판매회원 정보 조회
    public SellerInfoDTO selectSellerInfoById(int sellerInfoId) {
        log.info("sellerInfoId: " + sellerInfoId);
        Optional<SellerInfo> sellerInfo = sellerInfoRepository.findById(sellerInfoId);
        return modelMapper.map(sellerInfo, SellerInfoDTO.class);
    }



}
