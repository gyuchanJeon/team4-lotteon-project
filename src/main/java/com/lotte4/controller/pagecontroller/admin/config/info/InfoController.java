package com.lotte4.controller.pagecontroller.admin.config.info;

import com.lotte4.dto.admin.config.InfoDTO;
import com.lotte4.service.admin.config.InfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@Log4j2
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;
    //기본설정

    // 제목과 부제 수정    `
    @PostMapping("/admin/config/info")
    public ResponseEntity<InfoDTO> updateInfo(@RequestBody InfoDTO infoDto) {

        InfoDTO updatedInfo = infoService.updateInfoTitleAndSubtitle(infoDto);
        return ResponseEntity.ok(updatedInfo);
    }

    // 기업 정보 수정
    @PostMapping("/admin/config/company")
    public ResponseEntity<InfoDTO> updateCompanyInfo(@RequestBody InfoDTO infoDto) {

        InfoDTO updatedInfo = infoService.updateCompanyInfo(infoDto);
        return ResponseEntity.ok(updatedInfo);
    }

    // 고객센터 정보 수정
    @PostMapping("/admin/config/cs")
    public ResponseEntity<InfoDTO> updateCompanyCs(@RequestBody InfoDTO infoDto) {

        InfoDTO updatedInfo = infoService.updateCompanyCs(infoDto);
        return ResponseEntity.ok(updatedInfo);
    }

    // 카피라이트 정보 수정
    @PostMapping("/admin/config/copyright")
    public ResponseEntity<InfoDTO> updateCopyright(@RequestBody InfoDTO infoDto) {

        InfoDTO updatedInfo = infoService.updateCopyright(infoDto);
        return ResponseEntity.ok(updatedInfo);
    }

        // 로고 파일 업로드 처리
        @PostMapping("/admin/config/logo")
        public ResponseEntity<InfoDTO> uploadLogos(@RequestParam("headerLogo") MultipartFile headerLogo,
                                                   @RequestParam("footerLogo") MultipartFile footerLogo,
                                                   @RequestParam("favicon") MultipartFile favicon) {
            try {
                // 서비스 계층에서 파일 업로드 처리
                InfoDTO updatedInfo = infoService.uploadLogos(headerLogo, footerLogo, favicon);
                return ResponseEntity.ok(updatedInfo);
            } catch (IOException e) {
                log.error("파일 업로드 중 오류 발생: {}", e.getMessage());
                return ResponseEntity.status(500).body(null);  // 오류 발생 시 500 상태 반환
            }
        }
    }



