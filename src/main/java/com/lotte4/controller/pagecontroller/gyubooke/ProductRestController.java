/*
     날짜 : 2024/10/31
     이름 : 전규찬(최초 작성자)
     내용 : 상품 RestController 생성

     수정이력
      - 2024/10/31 전규찬 - 매핑 url REST 방식에 맞게 수정 / 상품 수정을 위한 메서드 추가
*/

package com.lotte4.controller.pagecontroller.gyubooke;

import com.lotte4.dto.*;
import com.lotte4.dto.coupon.CouponIssuedRequestDTO;
import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.User;
import com.lotte4.repository.CouponIssuedRepository;
import com.lotte4.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
public class ProductRestController {
    private final ModelMapper modelMapper;

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SellerInfoService sellerInfoService;
    private final CouponIssuedService couponIssuedService;

    public static List<Integer> stringToIntegerList(String string) {
        return ProductService.stringToIntegerList(string);
    }

    @GetMapping("/admin/product/{parentId}")
    public List<CateForProdRegisterDTO> registerCategory(@PathVariable int parentId) {
        return productService.getProductCateByParent(parentId);
    }


    @PostMapping(value = "/admin/product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Integer>> productRegisterWithCate(@RequestParam("cateId") int cateId,
                                                                        @RequestParam("sellerId") int sellerId,
                                                                        @RequestParam("img_1") MultipartFile img_1,
                                                                        @RequestParam("img_2") MultipartFile img_2,
                                                                        @RequestParam("img_3") MultipartFile img_3,
                                                                        @RequestParam("detail_") MultipartFile detail_,
                                                                        @RequestParam("optionsJson") String optionsJson,
                                                                        @RequestParam("product_Detail_Id") int product_Detail_Id,
                                                                        @ModelAttribute ProductDTO productDTO) {

        log.info("productdto = " + productDTO);
        log.info("product_Detail_Id = " + product_Detail_Id);

        // 상품 카테고리 아이디 입력
        productDTO.setProductCateId(categoryService.getProductCate(cateId));

        // 판매자 정보 입력
        SellerInfoDTO sellerInfo = sellerInfoService.selectSellerInfoById(sellerId);
        log.info("sellerInfo = " + sellerInfo);

        // 상품 상세 정보 입력
        ProductDetailDTO productDetail = productService.getProductDetailById(product_Detail_Id);
        log.info("productDetail = " + productDetail);

        // 옵션을 LinkedHashMap으로 변환 후 productDTO에 주입
        ProductDTO productDTO1 = productService.JsonToMapAndSetProductDTO(optionsJson, productDTO);

        String img1 = productService.uploadAndDeleteProdImg(img_1, null);
        String img2 = productService.uploadAndDeleteProdImg(img_2, null);
        String img3 = productService.uploadAndDeleteProdImg(img_3, null);
        String detail = productService.uploadAndDeleteProdImg(detail_, null);

        productDTO1.setImg1(img1);
        productDTO1.setImg2(img2);
        productDTO1.setImg3(img3);
        productDTO1.setDetail(detail);
        productDTO1.setSellerInfoId(sellerInfo);
        productDTO1.setProductDetailId(productDetail);

        ProductDTO dto = productService.insertProduct(productDTO1);
        log.info("dto = " + dto);

        Map<String, Integer> response1 = new HashMap<>();

        if (dto != null) {
            int productId = dto.getProductId();
            response1.put("productId", productId);
            return ResponseEntity.ok().body(response1);
        }

        response1.put("productId", 0);
        return ResponseEntity.ok().body(response1);
    }

    @PostMapping("/admin/product/detail")
    public ResponseEntity<Map<String, Integer>> productDetailRegister(@RequestBody ProductDetailDTO productDetailDTO) {
        log.info(productDetailDTO);
        ProductDetailDTO dto = productService.insertProductDetail(productDetailDTO);
        Map<String, Integer> response = new HashMap<>();

        if (dto != null) {
            response.put("productDetailId", dto.getProductDetailId());
            return ResponseEntity.ok().body(response);
        }

        response.put("productDetailId", 0);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/admin/product/more")
    public void productRegisterMore(@RequestParam String prodONames,
                                    @RequestParam String prodPrices,
                                    @RequestParam String prodStocks,
                                    @RequestParam String mixedValuesList,
                                    @RequestParam String optionNames,
                                    @RequestParam String productId
    ) {
        productService.makeVariantDTOAndInsert(optionNames, prodONames, prodPrices, prodStocks, mixedValuesList, productId);
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<Map<String, String>> productDelete(@PathVariable int productId) {

        String status = productService.deleteProductAndImagesById(productId);
        Map<String, String> response = new HashMap<>();

        log.info(status);
        if (status.equals("success")) {
            response.put("status", "success");
        } else {
            response.put("status", "failure");
        }
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/admin/product")
    public ResponseEntity<Map<String, String>> selectedProductDelete(@RequestParam String productIds) {
        log.info("productIds = " + productIds);

        Map<String, String> response = new HashMap<>();
        int failure = 0;
        List<Integer> productIdList = stringToIntegerList(productIds);
        for (Integer productId : productIdList) {
            String status = productService.deleteProductAndImagesById(productId);
            if (!status.equals("success")) {
                failure++;
            }
        }

        if (failure > 0) {
            response.put("status", "failure");
        } else {
            response.put("status", "success");
        }

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/admin/product")
    public ResponseEntity<Map<String, String>> productModify(@RequestParam("sellerId") int sellerId,
                                                             @RequestParam(value = "img_1", required = false) MultipartFile img_1,
                                                             @RequestParam(value = "img_2", required = false) MultipartFile img_2,
                                                             @RequestParam(value = "img_3", required = false) MultipartFile img_3,
                                                             @RequestParam(value = "detail_", required = false) MultipartFile detail_,
                                                             @RequestParam("optionsJson") String optionsJson,
                                                             @ModelAttribute ProductDTO productDTO) {

        log.info("img_1 = " + img_1);
        log.info("img_2 = " + img_2);
        log.info("img_3 = " + img_3);
        log.info("detail_ = " + detail_);
        log.info("optionsJson = " + optionsJson);
        log.info("productDTO = " + productDTO);

        Product_V_DTO product_v_dto = productService.getProduct_V_ById(productDTO.getProductId());
        productDTO.setProductCateId(product_v_dto.getProductCateId());

        ProductDTO productDTO1 = productService.JsonToMapAndSetProductDTO(optionsJson, productDTO);
        log.info("productDTO1 = " + productDTO1);

        ProductDTO productDTO2 = productService.updateProdImg(img_1, img_2, img_3, detail_, productDTO, product_v_dto);

        log.info("productDTO2 = " + productDTO2);

        // 판매자 정보 입력
        SellerInfoDTO sellerInfo = sellerInfoService.selectSellerInfoById(sellerId);
        productDTO2.setSellerInfoId(sellerInfo);
        productDTO2.setProductDetailId(product_v_dto.getProductDetailId());

        ProductDTO dto = productService.insertProduct(productDTO2);

        Map<String, String> response = new HashMap<>();

        if (dto != null) {
            response.put("status", "success");
            return ResponseEntity.ok().body(response);
        }

        response.put("status", "failure");

        return ResponseEntity.ok().body(response);
    }


    @PutMapping("/admin/product/detail")
    public ResponseEntity<Map<String, String>> productDetailModify(@RequestBody ProductDetailDTO productDetailDTO) {
        log.info(productDetailDTO);
        ProductDetailDTO dto = productService.insertProductDetail(productDetailDTO);
        Map<String, String> response = new HashMap<>();

        if (dto != null) {
            response.put("status", "success");
            return ResponseEntity.ok().body(response);
        }

        response.put("status", "failure");

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/admin/product/more")
    public void productModifyMore(@RequestParam String prodONames,
                                  @RequestParam String prodPrices,
                                  @RequestParam String prodStocks,
                                  @RequestParam String variantsIds,
                                  @RequestParam String valuesList,
                                  @RequestParam String optionNames,
                                  @RequestParam String productId) {

        log.info("prodONames = " + prodONames);
        log.info("prodPrices = " + prodPrices);
        log.info("prodStocks = " + prodStocks);
        log.info("variantsIds = " + variantsIds);
        log.info("valuesList = " + valuesList);
        log.info("optionNames = " + optionNames);
        log.info("productId = " + productId);

        productService.makeVariantDTOAndUpdate(prodONames, prodPrices, prodStocks, variantsIds, valuesList, optionNames, productId);
    }

    @PostMapping("/api/coupons/issue")
    public ResponseEntity<String> issueCoupon(@RequestBody CouponIssuedRequestDTO request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        String uid = principal.getName();
        request.setUid(uid);

        try {
            boolean issued = couponIssuedService.issueCoupon(request);
            if (issued) {
                return ResponseEntity.ok("쿠폰이 성공적으로 발급되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 발급된 쿠폰입니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("쿠폰 발급 중 오류가 발생했습니다.");
        }
    }
}