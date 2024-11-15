package com.lotte4.service;

import com.lotte4.dto.admin.config.RecruitDTO;
import com.lotte4.entity.Recruit;
import com.lotte4.repository.admin.config.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final ModelMapper modelMapper;


    public List<RecruitDTO> getAllRecruits() {
        List<RecruitDTO> recruitList = recruitRepository.findAll().stream()
                .map(recruit -> {
                    RecruitDTO recruitDTO = modelMapper.map(recruit, RecruitDTO.class);
                    recruitDTO.updateStatus();
                    recruitDTO.formatDates();
                    return recruitDTO;
                })
                .collect(Collectors.toList());

        // 로그 추가
        recruitList.forEach(recruit -> System.out.println("Recruit ID: " + recruit.getRecruitId()));

        return recruitList;
    }

    public Page<RecruitDTO> getRecruitsPage(Pageable pageable) {
        return recruitRepository.findAll(pageable)
                .map(recruit -> {
                    RecruitDTO recruitDTO = modelMapper.map(recruit, RecruitDTO.class);
                    System.out.println("sDate: " + recruit.getSDate() + ", eDate: " + recruit.getEDate()); // 매핑 후 status 값 확인
                    recruitDTO.updateStatus();
                    recruitDTO.formatDates();
                    return recruitDTO;
                });
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Transactional
    public void updateExpiredRecruitStatus() {
        List<Recruit> expiredRecruits = recruitRepository.findExpiredRecruits(LocalDate.now(), "모집중");
        expiredRecruits.forEach(recruit -> recruit.setStatus("마감"));
        recruitRepository.saveAll(expiredRecruits); // 데이터베이스에 저장하여 업데이트 적용
    }

    public RecruitDTO addRecruit(RecruitDTO recruitDTO) {
        // 디버깅 로그 추가
        System.out.println("RecruitDTO data: " + recruitDTO);

        recruitDTO.setStatus("모집중");
        recruitDTO.updateStatus();

        Recruit recruit = modelMapper.map(recruitDTO, Recruit.class);
        Recruit savedRecruit = recruitRepository.save(recruit);

        System.out.println("Saved Recruit: " + savedRecruit); // 저장된 엔티티 로그 출력
        return modelMapper.map(savedRecruit, RecruitDTO.class);
    }


    private Specification<Recruit> buildSearchSpecification(String division, String career, String employment, String searchText) {
        Specification<Recruit> spec = Specification.where(null);

        if (division != null && !division.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("division"), division));
        }
        if (career != null && !career.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("career"), career));
        }
        if (employment != null && !employment.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("employment"), employment));
        }
        if (searchText != null && !searchText.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("title"), "%" + searchText + "%"));
        }

        return spec;
    }

    // /company/recruit에서 사용할 검색 메서드
    public Page<RecruitDTO> searchRecruitsForCompany(String division, String career, String employment, String searchText, Pageable pageable) {
        Specification<Recruit> spec = buildSearchSpecification(division, career, employment, searchText);
        Page<Recruit> recruits = recruitRepository.findAll(spec, pageable);

        return recruits.map(recruit -> {
            RecruitDTO recruitDTO = modelMapper.map(recruit, RecruitDTO.class);
            recruitDTO.formatDates();
            recruitDTO.updateStatus();
            return recruitDTO;
        });
    }

    // /cs/recruit/list에서 사용할 검색 메서드
    public Page<RecruitDTO> searchRecruitsForCs(String searchCategory, String searchText, Pageable pageable) {
        Specification<Recruit> spec = Specification.where(null);

        if (searchCategory != null && searchText != null && !searchText.isEmpty()) {
            switch (searchCategory) {
                case "번호":
                    spec = spec.and((root, query, cb) -> cb.equal(root.get("recruitId"), searchText));
                    break;
                case "채용부서":
                    spec = spec.and((root, query, cb) -> cb.equal(root.get("division"), searchText));
                    break;
                case "채용형태":
                    spec = spec.and((root, query, cb) -> cb.equal(root.get("employment"), searchText));
                    break;
                case "제목":
                    spec = spec.and((root, query, cb) -> cb.like(root.get("title"), "%" + searchText + "%"));
                    break;
                default:
                    break;
            }
        }

        Page<Recruit> recruits = recruitRepository.findAll(spec, pageable);
        System.out.println("검색 결과 개수: " + recruits.getTotalElements()); // 검색된 총 개수 출력

        return recruits.map(recruit -> {
            RecruitDTO recruitDTO = modelMapper.map(recruit, RecruitDTO.class);
            recruitDTO.formatDates();
            recruitDTO.updateStatus();
            return recruitDTO;
        });
    }




    @Transactional
    public void deleteRecruits(int recruitId) {
        recruitRepository.deleteById(recruitId);
    }


}
