package com.lotte4.service;

import com.lotte4.dto.AddressDTO;
import com.lotte4.dto.MemberInfoDTO;
import com.lotte4.dto.SellerInfoDTO;
import com.lotte4.dto.UserDTO;
import com.lotte4.entity.Address;
import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.User;
import com.lotte4.repository.MemberInfoRepository;
import com.lotte4.repository.SellerInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class MemberInfoService {

    private final MemberInfoRepository memberInfoRepository;
    private final ModelMapper modelMapper;

    // 일반회원 정보 저장
    public MemberInfo insertMemberInfo(MemberInfoDTO memberInfoDTO) {
        if (memberInfoDTO == null) {
            throw new IllegalArgumentException("MemberInfoDTO cannot be null");
        }

        log.info("memberInfoDTO: " + memberInfoDTO);
        MemberInfo memberInfo = memberInfoDTO.toEntity();
        return memberInfoRepository.save(memberInfo);
    }

    // 일반회원 정보 조회
    public Optional<MemberInfo> selectMemberInfoById(int memberInfoId) {
        log.info("memberInfoId: " + memberInfoId);
        return memberInfoRepository.findById(memberInfoId);
    }

    // member 사용자 조회
    public MemberInfoDTO selectMemberInfo(int memberInfoId) {
        return memberInfoRepository.findByMemberInfoId(memberInfoId)
                .map(memberInfo -> modelMapper.map(memberInfo, MemberInfoDTO.class))
                .orElse(null);
    }

    // 나의설정 정보 수정
    @Transactional
    public MemberInfoDTO updateMemberInfo(MemberInfoDTO memberInfoDTO) {

        log.info("memberInfoDTO: " + memberInfoDTO);

        // 기존 값을 가져옴
        MemberInfo existingMemberInfo = memberInfoRepository.findByMemberInfoId(memberInfoDTO.getMemberInfoId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        log.info("existingMemberInfo: " + existingMemberInfo);

        if(memberInfoDTO.getEmail()!=null){
            existingMemberInfo.setEmail(memberInfoDTO.getEmail());
        }
        if(memberInfoDTO.getHp()!=null){
            existingMemberInfo.setHp(memberInfoDTO.getHp());
        }
        // Address 객체 업데이트
        Address address = existingMemberInfo.getAddress(); // 기존 Address 객체 가져오기
        if (address == null) {
            address = new Address(); // Address 객체가 null인 경우 새로 생성
            existingMemberInfo.setAddress(address); // Member에 Address 설정
        }

        // AddressDTO에서 값 가져와 엔티티의 Address 속성 설정
        if (memberInfoDTO.getAddress() != null) {
            AddressDTO addressDTO = memberInfoDTO.getAddress();
            address.setZipCode(addressDTO.getZipCode() != null ? addressDTO.getZipCode() : address.getZipCode());
            address.setAddr1(addressDTO.getAddr1() != null ? addressDTO.getAddr1() : address.getAddr1());
            address.setAddr2(addressDTO.getAddr2() != null ? addressDTO.getAddr2() : address.getAddr2());
        }

        MemberInfo updatedMember = memberInfoRepository.save(existingMemberInfo);

        return updatedMember.toDTO();

    }

    // 관리자 회원수정
    @Transactional
    public void updateMember(MemberInfoDTO memberInfoDTO) {

        log.info("memberInfoDTO: " + memberInfoDTO);

        // 기존 회원 정보를 찾기
        MemberInfo memberInfo = memberInfoRepository.findByMemberInfoId(memberInfoDTO.getMemberInfoId())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 기존 MemberInfo 객체를 업데이트할 값들로 복사
        memberInfo = memberInfo.toBuilder()
                .name(memberInfoDTO.getName())
                .gender(memberInfoDTO.getGender())
                .email(memberInfoDTO.getEmail())
                .hp(memberInfoDTO.getHp())
                .etc(memberInfoDTO.getEtc())
                .build();

        // Address 객체 업데이트
        Address address = memberInfo.getAddress(); // 기존 Address 객체 가져오기
        if (address == null) {
            address = Address.builder().build(); // Address 객체가 null인 경우 새로 생성
            memberInfo.setAddress(address); // MemberInfo에 Address 설정
        }

        // AddressDTO에서 값 가져와 엔티티의 Address 속성 설정
        if (memberInfoDTO.getAddress() != null) {
            AddressDTO addressDTO = memberInfoDTO.getAddress();
            address = address.toBuilder()
                    .zipCode(addressDTO.getZipCode() != null ? addressDTO.getZipCode() : address.getZipCode())
                    .addr1(addressDTO.getAddr1() != null ? addressDTO.getAddr1() : address.getAddr1())
                    .addr2(addressDTO.getAddr2() != null ? addressDTO.getAddr2() : address.getAddr2())
                    .build();
            memberInfo.setAddress(address);
        }

        memberInfoRepository.save(memberInfo);
    }


    // 여러 사용자의 등급을 업데이트하는 메서드
    @Transactional
    public void updateMemberGrades(List<MemberInfoDTO> memberGrades) {
        log.info("memberGrades: " + memberGrades);

        // 각 MemberInfoDTO의 ID와 등급을 수집하여 업데이트
        for (MemberInfoDTO memberInfo : memberGrades) {
            int memberInfoId = memberInfo.getMemberInfoId(); // 단일 ID 가져옴
            String grade = memberInfo.getGrade(); // 해당 등급 가져옴

            // 해당 ID에 대해 등급을 업데이트하는 메서드 호출
            memberInfoRepository.updateGradeByMemberInfoId(memberInfoId, grade);
        }
    }






}


