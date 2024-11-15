package com.lotte4.controller.pagecontroller.admin.product;

import com.lotte4.dto.*;
import com.lotte4.security.MyUserDetails;
import com.lotte4.service.CategoryService;
import com.lotte4.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Controller
public class AdminProductController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    // 재귀를 이용하여 조합 생성
    private static void generateCombinations(List<List<String>> lists, List<String> resultList, int depth, List<String> current) {
        // 모든 리스트를 처리한 경우
        if (depth == lists.size()) {
            // 현재 조합을 결과 리스트에 추가
            resultList.add(String.join(" ", current));
            return;
        }

        // 현재 깊이의 리스트에서 각 요소를 선택
        for (String item : lists.get(depth)) {
            current.add(item); // 현재 요소 추가
            generateCombinations(lists, resultList, depth + 1, current); // 다음 리스트로 재귀 호출
            current.remove(current.size() - 1); // 현재 요소 제거 (백트래킹)
        }
    }

//    @GetMapping("/admin/product/list")
//    public String AdminProductList(Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
//        int sellerInfoId = userDetails.getUser().getSellerInfo().getSellerInfoId();
//        List<ProductDTO> products = productService.getAllProductBySellerId(sellerInfoId);
//        model.addAttribute("products", products);
//        return "/admin/product/list";
//    }

    // 상품현황
    @GetMapping("/admin/product/list")
    public String AdminProductList(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "searchCategory", required = false) String searchCategory, // searchCategory 추가
                                  @AuthenticationPrincipal MyUserDetails userDetails) {

        log.info("keyword: " + keyword);
        log.info("searchCategory: " + searchCategory);

        // 검색 조건에 따라 상품 목록을 가져옴
        Page<ProductDTO> productList = productService.selectProductListByRole(page, size, keyword, searchCategory, userDetails); // 검색 조건 추가

        // 시작 번호 계산 (검색된 결과에 따른 시작 번호)
        long totalElements = productList.getTotalElements();
        int startNo = (int) totalElements - (page * size);

        model.addAttribute("products", productList);
        model.addAttribute("totalPages", productList.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("startNo", startNo); // 시작 번호 추가
        model.addAttribute("keyword", keyword); // keyword를 모델에 추가
        model.addAttribute("searchCategory", searchCategory); // searchCategory를 모델에 추가

        log.info("products: " + productList);
        log.info("totalPages: " + productList.getTotalPages());
        log.info("currentPage: " + page);
        log.info("size: " + size);
        log.info("totalElements: " + totalElements);
        log.info("startNo: " + startNo);
        log.info("keyword: " + keyword);
        log.info("userList size: " + productList.getContent().size());

        return "/admin/product/list";
    }

    // 상품등록
    @GetMapping("/admin/product/register")
    public String AdminProductRegister(Model model) {
        model.addAttribute("productCate1List", categoryService.getProductCateListWithDepth(1));
        return "/admin/product/register";
    }

    // 상품 상세 등록
    @GetMapping("/admin/product/registerMore")
    public String AdminProductRegisterMore(int productId, Model model) {

        Product_V_DTO productDTO = productService.getProduct_V_ById(productId);
        int prodId = productDTO.getProductId();

        Map<String, List<String>> options = productDTO.getOptions();
        List<String> optionNames = new ArrayList<>();
        List<List<String>> optionValuesList = new ArrayList<>();

        // 각 옵션 키에 대한 반복문
        for (Map.Entry<String, List<String>> entry : options.entrySet()) {
            optionNames.add(entry.getKey());

            // 현재 옵션 키에 대한 값 리스트
            List<String> optionValues = entry.getValue();
            optionValuesList.add(optionValues);
        }

        List<String> resultList = new ArrayList<>();
        generateCombinations(optionValuesList, resultList, 0, new ArrayList<>());

        log.info("resultList = " + resultList);

        // String 조합들을 List<List<String>> 형태로 변환
        List<List<String>> mixedValuesList = new ArrayList<>();
        for (String combination : resultList) {
            List<String> individualList = Arrays.asList(combination.split(" ")); // 공백 기준으로 문자열을 나누어 List<String>으로 변환
            mixedValuesList.add(individualList);
        }


        model.addAttribute("optionNames", optionNames);
        model.addAttribute("mixedValuesList", mixedValuesList);
        model.addAttribute("prodId", prodId);

        return "/admin/product/registerMore";
    }


    // 상품수정
    @GetMapping("/admin/product/modify")
    public String AdminProductModify(int productId, Model model) {

        Product_V_DTO productDTO = productService.getProduct_V_ById(productId);
        int productCateId = productDTO.getProductCateId().getProductCateId();
        model.addAttribute("productDTO", productDTO);

        LinkedHashMap<String, List<String>> options = productDTO.getOptions();
        model.addAttribute("options", options);

        ProductDetailDTO productDetailDTO = productService.getProductDetailById(productDTO.getProductDetailId().getProductDetailId());
        model.addAttribute("productDetailDTO", productDetailDTO);

        ProductCateDTO productCateDTO = categoryService.getProductCate(productCateId);
        ProductCateDTO parentProductCateDTO = productCateDTO.getParent();
        int cateDepth = productCateDTO.getDepth();

        if (cateDepth == 2) {
            model.addAttribute("productCate", productCateDTO);
        } else if (cateDepth == 3) {
            model.addAttribute("productCate", productCateDTO);
            model.addAttribute("parentProductCate", parentProductCateDTO);
        }
        return "/admin/product/modify";
    }

    // 상품 상세 수정
    @GetMapping("/admin/product/modifyMore")
    public String AdminProductModifyMore(int productId, Model model) {

        Product_V_DTO productDTO = productService.getProduct_V_ById(productId);
        List<ProductVariantsWithoutProductDTO> productVariantsList = productDTO.getProductVariants();


        Map<String, List<String>> options = productDTO.getOptions();
        List<String> optionNames = new ArrayList<>();

        // 각 옵션 키에 대한 반복문
        for (Map.Entry<String, List<String>> entry : options.entrySet()) {
            optionNames.add(entry.getKey());
        }

        model.addAttribute("productId", productId);
        model.addAttribute("optionNames", optionNames);
        model.addAttribute("productVariantsList", productVariantsList);

        return "/admin/product/modifyMore";
    }


}
