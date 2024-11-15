package com.lotte4.controller.pagecontroller.admin.config;

import com.lotte4.dto.BannerDTO;
import com.lotte4.dto.ProductCateChildDTO;
import com.lotte4.dto.ProductRegisterCateDTO;
import com.lotte4.dto.TermsDTO;
import com.lotte4.dto.admin.config.InfoDTO;
import com.lotte4.dto.ProductCateDTO;
import com.lotte4.entity.ProductCate;
import com.lotte4.service.CategoryService;
import com.lotte4.service.TermsService;
import com.lotte4.service.admin.config.BannerService;
import lombok.extern.log4j.Log4j2;

import com.lotte4.dto.admin.config.VersionDTO;
import com.lotte4.entity.Info;
import com.lotte4.service.admin.config.InfoService;
import com.lotte4.service.admin.config.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
     날짜 : 2024/10/28
     이름 : 강은경
     내용 : ConfigController 생성

     수정이력
      - 2024/10/28 강은경 - terms select&update 메서드 추가
*/
@Log4j2
@RequiredArgsConstructor
@Controller
public class ConfigController {
    // View반환 Controller
    private final VersionService versionService;
    private final InfoService infoService;
    private final TermsService termsService;

    @GetMapping("/admin/config/info")
    public String AdminConfigInfoInsert(Model model) {

        InfoDTO infoDTO = infoService.selectInfoDTO();
        model.addAttribute("info", infoDTO);

        return "/admin/config/info";
    }

    private final CategoryService categoryService;
    private final BannerService bannerService;
    //배너관리
    @GetMapping("/admin/config/banner")
    public String AdminconfigBanner(Model model) {
        //기본 배너관리 페이지로 이동
        List<BannerDTO> bannerDTOS = bannerService.getBannersByLocation("MAIN1");
        model.addAttribute("configBanners", bannerDTOS);
        model.addAttribute("locationNow", "MAIN1");
        return "/admin/config/banner";
    }

    @GetMapping("/admin/config/banner/{location}")
    public String AdminconfigBannerWithLocation(@PathVariable String location, Model model) {
        List<BannerDTO> bannerDTOS = bannerService.getBannersByLocation(location);
        model.addAttribute("configBanners", bannerDTOS);
        model.addAttribute("locationNow", location);
        return "/admin/config/banner";
    }


    @PostMapping(value = "/admin/config/banner", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> AdminconfigBannerWithLocation(
            @RequestParam("bannerImg") MultipartFile bannerImg,
            @ModelAttribute BannerDTO bannerDTO, Model model) {

        try {
            String result = bannerService.insertBanner(bannerDTO,bannerImg);
            if(result.equals("success")) {
                return ResponseEntity.ok("success");
            }else{
                return ResponseEntity.ok("fail");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.ok("fail");
        }
    }

    @PutMapping("/admin/config/banner/enable")
    public ResponseEntity<String> AdminConfigBannerEnable(@RequestBody BannerDTO bannerDTO) {
        boolean expire = bannerService.expireBannerCheck(bannerDTO);
        if(expire) {
            return ResponseEntity.ok("fail");
        }else{
            bannerService.updateBannerState(bannerDTO,1);
            return ResponseEntity.ok("success");
        }
    }

    @PutMapping("/admin/config/banner/disable")
    public ResponseEntity<String> AdminConfigBannerDisable(@RequestBody BannerDTO bannerDTO) {
        bannerService.updateBannerState(bannerDTO,0);
        return ResponseEntity.ok("success");

    }

    @DeleteMapping("/admin/config/banner")
    public ResponseEntity<Map<String, Object>> AdminconfigBannerDelete(@RequestBody List<Integer> selectedItems, Model model) {

        Map<String, Object> response = new HashMap<>();

        log.info("bannerDelete : "+selectedItems);

        try {
            // JSON으로 받은 선택 항목 처리 로직
            for (Integer bannerId : selectedItems) {
                //삭제
                bannerService.deleteBanner(bannerId);
            }
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 발생 시 실패 응답
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    //약관 관리
    @GetMapping("/admin/config/policy")
    public String AdminconfigPolicy(Model model) {

        TermsDTO termsDTO = termsService.selectTerms();
        model.addAttribute("termsDTO", termsDTO);
        log.info("termsDTO : "+termsDTO);

        return "/admin/config/policy";
    }

    // 구매회원 약관 정보 수정
    @PostMapping("/admin/config/term")
    public ResponseEntity<TermsDTO> updateTerm(@RequestBody TermsDTO termsDTO) {
        log.info("termsDTO : "+termsDTO);
        TermsDTO updatedTerm = termsService.updateTerm(termsDTO);
        return ResponseEntity.ok(updatedTerm);

    }

    // 판매회원 약관 정보 수정
    @PostMapping("/admin/config/tax")
    public ResponseEntity<TermsDTO> updateTax(@RequestBody TermsDTO termsDTO) {

        TermsDTO updatedTax = termsService.updateTax(termsDTO);
        return ResponseEntity.ok(updatedTax);

    }

    // 전자금융거래 약관 정보 수정
    @PostMapping("/admin/config/finance")
    public ResponseEntity<TermsDTO> updateFinance(@RequestBody TermsDTO termsDTO) {

        TermsDTO updatedFinance = termsService.updateFinance(termsDTO);
        return ResponseEntity.ok(updatedFinance);

    }

    // 위치정보 약관 정보 수정
    @PostMapping("/admin/config/location")
    public ResponseEntity<TermsDTO> updateLocation(@RequestBody TermsDTO termsDTO) {

        TermsDTO updatedLocation = termsService.updateLocation(termsDTO);
        return ResponseEntity.ok(updatedLocation);

    }

    // 개인정보처리방침 약관 정보 수정
    @PostMapping("/admin/config/privacy")
    public ResponseEntity<TermsDTO> updatePrivacy(@RequestBody TermsDTO termsDTO) {

        TermsDTO updatedPrivacy = termsService.updatePrivacy(termsDTO);
        return ResponseEntity.ok(updatedPrivacy);

    }



    //버전관리
    @GetMapping("/admin/config/version")
    public String AdminconfigVersion(Model model) {
        List<VersionDTO> versions = versionService.selectAll();
        model.addAttribute("versions", versions);
        return "/admin/config/version";
    }


    // 카테고리
    @GetMapping("/admin/config/category")
    public String AdminConfigCategory(Model model) {
        List<ProductCateChildDTO> productCateDTOList = categoryService.getProductCateListWithDepth(1);
        model.addAttribute("productCateDTOList", productCateDTOList);
        return "/admin/config/category";
    }

    @PostMapping("/admin/config/category")
    @ResponseBody
    public String AdminConfigCategoryPost(@RequestBody ProductRegisterCateDTO productRegisterCateDTO, @RequestParam String parent, Model model) {
        log.info("productCateDTO : "+productRegisterCateDTO);
        log.info("parent : "+parent);

        categoryService.insertProductCate(productRegisterCateDTO, parent);
        return "success";
    }

    @PutMapping("/admin/config/category")
    public ResponseEntity<String> updateCategoryOrder(@RequestBody List<Map<String, Object>> changes) {
        // changes 리스트를 처리하여 카테고리 순서 업데이트 로직 추가
        // 각 Map에는 'name', 'order', 'depth' 등이 포함됨

        log.info("changes: "+changes);
        boolean result = categoryService.updateProductCateOrder(changes);
        if(result) {
            // 성공적인 업데이트 후 응답
            return ResponseEntity.ok("카테고리 순서가 업데이트되었습니다.");
        }
        else{
            // 실패한 경우, 400 Bad Request 상태 코드와 메시지 반환
            return ResponseEntity.badRequest().body("카테고리 순서 업데이트에 실패했습니다.");
        }
    }


    @DeleteMapping("/admin/config/category")
    @ResponseBody
    public String AdminConfigCategoryDelete(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");

        log.info("name : "+name);
        boolean value = categoryService.deleteProductCate(name);
        if(value){
            return "success";
        }
        else{
            return "fail";
        }
    }



}
