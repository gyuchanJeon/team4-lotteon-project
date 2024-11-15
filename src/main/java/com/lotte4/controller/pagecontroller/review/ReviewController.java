package com.lotte4.controller.pagecontroller.review;


import com.lotte4.security.MyUserDetails;
import com.lotte4.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import com.lotte4.dto.mongodb.ReviewDTO;
import com.lotte4.service.mongodb.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Log4j2
public class ReviewController {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final HttpServletRequest httpServletRequest;

    @GetMapping("/review")
    public ResponseEntity<List<ReviewDTO>> findAllReviews() {

        List<ReviewDTO> reviewDocuments = reviewService.findAllReview();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reviewDocuments);
    }

    @GetMapping("/review/{prodId}")
    public ResponseEntity<Map<String, Object>> findReviewsByProdId(
            @PathVariable int prodId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewDTO> reviewDocuments = reviewService.findReviewByProdId(prodId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", reviewDocuments.getContent());
        response.put("currentPage", reviewDocuments.getNumber());
        response.put("totalPages", reviewDocuments.getTotalPages());
        response.put("totalItems", reviewDocuments.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping(value = "/review", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity insertReview(@ModelAttribute ReviewDTO review1,
                                               @RequestParam(required = false) MultipartFile image1,
                                               @RequestParam(required = false) MultipartFile image2,
                                               @AuthenticationPrincipal MyUserDetails myUserDetails,
                                               HttpServletRequest request
                                                  ) {

        log.info("review1 = " + review1);

        // uid 주입
        review1.setUid(myUserDetails.getUser().getUid());

        // variantId로 productId 찾고 주입하기
        int productId = productService.getProductIdByVariantId(review1.getVariantId());
        review1.setProdId(productId);

        // ip 주소 찾아서 주입하기
        review1.setRegIp(request.getRemoteAddr());

        // 이미지 파일 업로드하고 이름 주입하기
        if (image1 != null) {
            String image1Name = reviewService.uploadReviewImage(image1);
            review1.setImg1(image1Name);
        }
        if (image2 != null) {
            String image2Name = reviewService.uploadReviewImage(image2);
            review1.setImg2(image2Name);
        }

        // 현재 시각 주입하기
        review1.setRegDate(LocalDateTime.now());

        log.info("최종 review1 = " + review1);

        ReviewDTO savedReview = reviewService.insertReview(review1);

        log.info("savedReview = " + savedReview);

        if (savedReview != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping("/reviews/{uid}")
    public ResponseEntity<Boolean> deleteReview(@PathVariable("uid") String uid) {

        boolean result = reviewService.deleteReview(uid);

        return ResponseEntity
                .ok()
                .body(result);
    }


}
