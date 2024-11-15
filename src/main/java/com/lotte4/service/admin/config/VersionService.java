package com.lotte4.service.admin.config;

import com.lotte4.dto.admin.config.VersionDTO;
import com.lotte4.entity.Version;
import com.lotte4.repository.admin.config.InfoRepository;
import com.lotte4.repository.admin.config.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class VersionService {
    private final VersionRepository versionRepository;
    private final ModelMapper modelMapper;

    public VersionDTO insertVersion(VersionDTO versionDTO){
        Version savedVersion = versionRepository.save(modelMapper.map(versionDTO, Version.class));
        return modelMapper.map(savedVersion, VersionDTO.class);
    }
    public boolean selectVersionById(int id){
        return versionRepository.findById(id).isPresent();
    }
    public List<VersionDTO> selectAll() {
        // 반환할 리스트 생성
        List<VersionDTO> versionDTOList = new ArrayList<>();

        // 엔티티 리스트를 DTO로 변환하여 리스트에 추가
        versionRepository.findAll().forEach(version -> {
            VersionDTO versionDTO = modelMapper.map(version, VersionDTO.class);
            versionDTOList.add(versionDTO);
        });

        // DTO 리스트 반환
        return versionDTOList;
    }
    public boolean deleteVersion(int versionId) {
        if (versionRepository.existsById(versionId)) {
            versionRepository.deleteById(versionId);
            return !versionRepository.existsById(versionId); // 삭제 후 확인
        } else {
            throw new IllegalArgumentException("해당 버전이 존재하지 않습니다.");
        }
    }
}