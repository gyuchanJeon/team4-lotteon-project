package com.lotte4.controller.pagecontroller.admin.config;
import com.lotte4.dto.admin.config.VersionDTO;
import com.lotte4.service.admin.config.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@RestController

public class VersionController {

    private final VersionService versionService;

    @PostMapping("/admin/config/version")
    public ResponseEntity<VersionDTO> InsertConfigVersion(@RequestBody VersionDTO versionDTO) {
        VersionDTO savedVersion = versionService.insertVersion(versionDTO);
        return ResponseEntity.ok(savedVersion);
    }

    @DeleteMapping("/admin/config/version/delete/{versionId}")
    public ResponseEntity<Void> deleteVersion(@PathVariable int versionId) {
        try {
            // 삭제하려는 버전이 존재하는지 확인
            if (!versionService.selectVersionById(versionId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 데이터가 없으면 404 응답
            }
            versionService.deleteVersion(versionId); // 서비스에서 삭제 로직 처리

            return ResponseEntity.ok().build(); // 성공 시 200 OK 응답

        } catch (Exception e) {
            // 삭제 중 예외가 발생한 경우 500 에러 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    }
