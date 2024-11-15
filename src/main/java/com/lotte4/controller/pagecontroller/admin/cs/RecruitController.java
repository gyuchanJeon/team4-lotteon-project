package com.lotte4.controller.pagecontroller.admin.cs;

import com.lotte4.dto.admin.config.RecruitDTO;
import com.lotte4.service.RecruitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
public class RecruitController {

    private final RecruitService recruitService;

    @GetMapping("/admin/cs/recruit/list")
    public String getRecruitList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String searchCategory,
            @RequestParam(required = false) String searchText,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // 항상 페이징 결과를 반환하도록 설정
        Page<RecruitDTO> recruitPage = recruitService.searchRecruitsForCs(searchCategory, searchText, pageable);

        // 페이징 관련 데이터 모델에 추가
        model.addAttribute("recruitPage", recruitPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", recruitPage.getTotalPages());

        return "/admin/cs/recruit/list";
    }



    @GetMapping("/company/recruit")
    public String companyRecruitPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) String career,
            @RequestParam(required = false) String employment,
            @RequestParam(required = false) String searchText,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RecruitDTO> recruitPage = recruitService.searchRecruitsForCompany(division, career, employment, searchText, pageable);

        model.addAttribute("recruitPage", recruitPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", recruitPage.getTotalPages());
        return "/company/recruit";
    }




    @PostMapping("/cs/recruit/list")
    @ResponseBody
    public ResponseEntity<String> addRecruit(@RequestBody RecruitDTO recruitDTO) {
        try {
            System.out.println("Received DTO: " + recruitDTO);
            if (recruitDTO.getSDate() == null) {
                System.out.println("sDate is null.");
            }
            if (recruitDTO.getEDate() == null) {
                System.out.println("eDate is null.");
            }
            recruitService.addRecruit(recruitDTO);
            return ResponseEntity.ok("채용 정보가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록에 실패하였습니다.");
        }
    }

    @DeleteMapping("/cs/recruit/list")
    public ResponseEntity<Map<String, Object>> deleteRecruits(@RequestBody List<Integer> recruitIds, Model model) {

        Map<String, Object> response = new HashMap<>();

        log.info("Received IDs for deletion: " + recruitIds); // 로그 확인

        try {
            for (Integer recruitId : recruitIds) {
            // recruitIds 전체를 deleteRecruits 메서드에 전달
            recruitService.deleteRecruits(recruitId);
            }

            response.put("success", true);
            response.put("message", "선택한 항목이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력
            response.put("success", false);
            response.put("message", "삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
