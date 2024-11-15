package com.lotte4.service;

import com.lotte4.dto.TermsDTO;
import com.lotte4.dto.UserDTO;
import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.SellerInfo;
import com.lotte4.entity.Terms;
import com.lotte4.entity.User;
import com.lotte4.repository.TermsRepository;
import com.lotte4.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.core.query.Term;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
     날짜 : 2024/10/28
     이름 : 강은경
     내용 : TermsService 생성

     수정이력
      - 2024/10/28 강은경 - terms select&update 메서드 추가
*/
@Log4j2
@RequiredArgsConstructor
@Service
public class TermsService {

    private final TermsRepository termsRepository;
    private final ModelMapper modelMapper;

    public TermsDTO selectTerms() {

        // entity를 dto로 변환
        return modelMapper.map(termsRepository.findByTermsId(1), TermsDTO.class);
    }

    // 구매회원 약관 update
    @Transactional
    public TermsDTO updateTerm(TermsDTO termsDTO) {

        int resultRow = termsRepository.updateTerm(
                termsDTO.getTerm()
        );
        if(resultRow == 1) {
            return termsDTO;
        }else
            return null;

    }

    // 판매회원 약관 update
    @Transactional
    public TermsDTO updateTax(TermsDTO termsDTO) {

        int resultRow = termsRepository.updateTax(
                termsDTO.getTax()
        );
        if(resultRow == 1) {
            return termsDTO;
        }else
            return null;

    }

    // 전자금융거래 약관 update
    @Transactional
    public TermsDTO updateFinance(TermsDTO termsDTO) {

        int resultRow = termsRepository.updateFinance(
                termsDTO.getFinance()
        );
        if(resultRow == 1) {
            return termsDTO;
        }else
            return null;

    }

    // 위치정보 이용약관 update
    @Transactional
    public TermsDTO updateLocation(TermsDTO termsDTO) {

        int resultRow = termsRepository.updateLocation(
                termsDTO.getLocation()
        );
        if(resultRow == 1) {
            return termsDTO;
        }else
            return null;

    }

    // 개인정보처리방침 update
    @Transactional
    public TermsDTO updatePrivacy(TermsDTO termsDTO) {

        int resultRow = termsRepository.updatePrivacy(
                termsDTO.getPrivacy()
        );
        if(resultRow == 1) {
            return termsDTO;
        }else
            return null;

    }

    public List<String[]> selectTerm(int type) {
        List<String[]> strList = new ArrayList<>();

        Terms term = termsRepository.findByTermsId(1);

        String target = "";


        Pattern pattern = null;
        Matcher matcher = null;

        switch (type){
            case 1:
                target = term.getTerm();
                pattern = Pattern.compile("(제\\d+조)([\\s\\S]+?)(?=제\\d+조|$)", Pattern.DOTALL);
                matcher = pattern.matcher(target);
                break;
            case 2:
                target = term.getTax();
                pattern = Pattern.compile("(제\\d+조)([\\s\\S]+?)(?=제\\d+조|$)", Pattern.DOTALL);
                matcher = pattern.matcher(target);
                break;
            case 3:
                target = term.getFinance();
                pattern = Pattern.compile("(제\\d+조)([\\s\\S]+?)(?=제\\d+조|$)", Pattern.DOTALL);
                matcher = pattern.matcher(target);
               break;
            case 4:
                target = term.getLocation();
                pattern = Pattern.compile("(제\\d+조)([\\s\\S]+?)(?=제\\d+조|$)", Pattern.DOTALL);
                matcher = pattern.matcher(target);
                break;
            case 5:
                target = term.getPrivacy();
                pattern = Pattern.compile("(제\\d+장)([\\s\\S]+?)(?=제\\d+장|$)", Pattern.DOTALL);
                matcher = pattern.matcher(target);
                break;
        }

        while (matcher.find()) {
            // 제목 추출
            String title = matcher.group(1).trim();
            // 내용 추출 (제목 이후 텍스트)
            String content = matcher.group(2).trim();

            String[] str = new String[]{title, content};
            strList.add(str);

        }
        return strList;
    }



}
