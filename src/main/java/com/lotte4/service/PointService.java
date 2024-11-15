package com.lotte4.service;

import com.lotte4.dto.PointDTO;
import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.Point;
import com.lotte4.entity.User;
import com.lotte4.repository.PointRepository;
import com.lotte4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
@Log4j2
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final ModelMapper modelMapper;

    // 모든 포인트 조회
    public Page<PointDTO> selectAllPoints(Pageable pageable) {
        return pointRepository.findAll(pageable).map(this::mapToPointDTO);
    }

    // (Type | Keyword Search) 통합 메서드
    public Page<PointDTO> searchPoints(String type, String searchType, String keyword, Pageable pageable) {
        if (type != null && !type.isEmpty() && searchType != null && keyword != null && !keyword.isEmpty()) {
            return findByTypeAndSearchType(type, searchType, keyword, pageable);
        } else if (type != null && !type.isEmpty()) {
            return searchByType(type, pageable);
        } else if (searchType != null && keyword != null && !searchType.isEmpty()) {
            return findBySearchTypeOnly(searchType, keyword, pageable);
        }
        return selectAllPoints(pageable);
    }
    // ( Type )
    public Page<PointDTO> searchByType(String type, Pageable pageable) {
        return pointRepository.findPointsByTypeOrderByPointDateDesc(type, pageable)
                .map(this::mapToPointDTO);
    }
    // ( Keyword Search )
    private Page<PointDTO> findBySearchTypeOnly(String searchType, String keyword, Pageable pageable) {
        switch (searchType) {
            case "id":
                return selectPointsByUid(keyword, pageable);
            case "email":
                return pointRepository.findByMemberInfo_EmailContainingOrderByPointDateDesc(keyword, pageable)
                        .map(this::mapToPointDTO);
            case "name":
                return pointRepository.findByMemberInfo_NameContainingOrderByPointDateDesc(keyword, pageable)
                        .map(this::mapToPointDTO);
            case "hp":
                return pointRepository.findByMemberInfo_HpContainingOrderByPointDateDesc(keyword, pageable)
                        .map(this::mapToPointDTO);
            case "all":
                return findBySearchTypeForAll(keyword, pageable);
            default:
                return Page.empty(pageable);
        }
    }
    // ( Type | Keyword Search)
    private Page<PointDTO> findByTypeAndSearchType(String type, String searchType, String keyword, Pageable pageable) {
        switch (searchType) {
            case "id":
                return selectPointsByTypeAndUid(type, keyword, pageable);
            case "email":
                return pointRepository.findByTypeAndMemberInfo_EmailContainingOrderByPointDateDesc(type, keyword, pageable)
                        .map(this::mapToPointDTO);
            case "name":
                return pointRepository.findByTypeAndMemberInfo_NameContainingOrderByPointDateDesc(type, keyword, pageable)
                        .map(this::mapToPointDTO);
            case "hp":
                return pointRepository.findByTypeAndMemberInfo_HpContainingOrderByPointDateDesc(type, keyword, pageable)
                        .map(this::mapToPointDTO);
            case "all":
                return findByTypeAndSearchTypeForAll(type, keyword, pageable);
            default:
                return Page.empty(pageable);
        }
    }


    // for case:all 메서드
    private Page<PointDTO> findBySearchTypeForAll(String keyword, Pageable pageable) {
        // 각 조건별 검색 수행
        List<List<PointDTO>> allResults = Arrays.asList(
                pointRepository.findByMemberInfo_EmailContainingOrderByPointDateDesc(keyword, Pageable.unpaged())
                        .map(this::mapToPointDTO).getContent(),
                pointRepository.findByMemberInfo_NameContainingOrderByPointDateDesc(keyword, Pageable.unpaged())
                        .map(this::mapToPointDTO).getContent(),
                pointRepository.findByMemberInfo_HpContainingOrderByPointDateDesc(keyword, Pageable.unpaged())
                        .map(this::mapToPointDTO).getContent(),
                selectPointsByTypeAndUidWithoutPage(null, keyword)
        );
        return getPageWithUniqueResults(allResults, pageable);
    }
    private Page<PointDTO> findByTypeAndSearchTypeForAll(String type, String keyword, Pageable pageable) {
        // 각 조건별 검색 수행
        List<List<PointDTO>> allResults = Arrays.asList(
                pointRepository.findByTypeAndMemberInfo_EmailContainingOrderByPointDateDesc(type, keyword, Pageable.unpaged())
                        .map(this::mapToPointDTO).getContent(),
                pointRepository.findByTypeAndMemberInfo_NameContainingOrderByPointDateDesc(type, keyword, Pageable.unpaged())
                        .map(this::mapToPointDTO).getContent(),
                pointRepository.findByTypeAndMemberInfo_HpContainingOrderByPointDateDesc(type, keyword, Pageable.unpaged())
                        .map(this::mapToPointDTO).getContent(),
                selectPointsByTypeAndUidWithoutPage(type, keyword)
        );
        return getPageWithUniqueResults(allResults, pageable);

    }
    // for case:all 중복 제거 메서드
    private Page<PointDTO> getPageWithUniqueResults(List<List<PointDTO>> allResults, Pageable pageable) {
        // 이중 리스트를 평탄화하고 중복 제거
        Map<Integer, PointDTO> uniqueMap = allResults.stream()
                .flatMap(List::stream)                    // 이중 리스트를 단일 리스트로 평탄화
                .collect(Collectors.toMap(
                        PointDTO::getPointId,             // pointId를 키로 사용
                        point -> point,                   // PointDTO 객체 자체를 값으로 사용
                        (existing, replacement) -> existing // 중복 발생 시 기존 값 유지
                ));

        // 중복이 제거된 결과를 List로 변환
        List<PointDTO> uniqueResults = new ArrayList<>(uniqueMap.values());

        // 페이지네이션 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), uniqueResults.size());
        List<PointDTO> pageContent = uniqueResults.subList(start, end);

        return new PageImpl<>(pageContent, pageable, uniqueResults.size());
    }
    // for case:all uid 조회 메서드 - paging 없이
    public List<PointDTO> selectPointsByTypeAndUidWithoutPage(String type, String uid) {
        List<User> users = userRepository.findByUidContaining(uid);
        if (type != null & uid != null) {
            return users.stream()
                    .flatMap(user -> pointRepository.findPointsByMemberInfoAndTypeOrderByPointDateDesc(user.getMemberInfo(), type, Pageable.unpaged())
                            .map(this::mapToPointDTO)
                            .getContent()
                            .stream())
                    .collect(Collectors.toList());
        } else if (uid != null & type == null) {
            return users.stream()
                    .flatMap(user -> pointRepository.findPointsByMemberInfoOrderByPointDateDesc(user.getMemberInfo(), Pageable.unpaged())
                            .map(this::mapToPointDTO)
                            .getContent()
                            .stream())
                    .collect(Collectors.toList());
        }
        return null;
    }


    // My page - Uid 별 모든 포인트 조회
    public Page<PointDTO> selectPointsByUid(String uid, Pageable pageable) {
        List<User> users = userRepository.findByUidContaining(uid);

        if (users.isEmpty()) {
            throw new NoSuchElementException("User not found with uid: " + uid);
        }

        List<PointDTO> allPoints = users.stream()
                .flatMap(user -> pointRepository.findPointsByMemberInfoOrderByPointDateDesc(user.getMemberInfo(), Pageable.unpaged())
                        .map(this::mapToPointDTO)
                        .getContent()
                        .stream())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allPoints.size());
        List<PointDTO> pageContent = allPoints.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allPoints.size());
    }
    // My page - ( Type | Uid )
    public Page<PointDTO> selectPointsByTypeAndUid(String type, String uid, Pageable pageable) {
        List<User> users = userRepository.findByUidContaining(uid);

        if (users.isEmpty()) {
            return Page.empty(pageable);
        }

        List<PointDTO> allPoints = users.stream()
                .flatMap(user -> pointRepository.findPointsByMemberInfoAndTypeOrderByPointDateDesc(user.getMemberInfo(), type, Pageable.unpaged())
                        .map(this::mapToPointDTO)
                        .getContent()
                        .stream())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allPoints.size());
        List<PointDTO> pageContent = allPoints.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allPoints.size());
    }


    // DTO - setUid()넣는 메서드
    private PointDTO mapToPointDTO(Point point) {
        PointDTO pointDTO = modelMapper.map(point, PointDTO.class);
        String uid = userRepository.findByMemberInfo(point.getMemberInfo()).getUid();
        pointDTO.setUid(uid);
        return pointDTO;
    }
}