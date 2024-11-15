/*
     날짜 : 2024/11/06
     이름 : 황수빈
     내용 : ReviewService 생성

     수정이력
      - 2024/11/07 전규찬 - insertReview 메서드 생성
*/

package com.lotte4.service.mongodb;

import com.lotte4.document.ReviewDocument;

import com.lotte4.dto.mongodb.ReviewDTO;

import com.lotte4.entity.ProductVariants;
import com.lotte4.repository.ProductVariantsRepository;
import com.lotte4.repository.mongodb.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ModelMapper modelMapper;

    private final ProductVariantsRepository productVariantsRepository;

    private final ReviewRepository reviewRepository;

    // 모든 리뷰 조회
    public List<ReviewDTO> findAllReview() {
        List<ReviewDocument> reviewList = reviewRepository.findAll();

        // Document 리스트를 DTO 리스트로 변환
        return reviewList.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());
    }


    public Page<ReviewDTO> findReviewByProdId(int prodId, Pageable pageable) {
        Page<ReviewDocument> reviewPage = reviewRepository.findByProdId(prodId, pageable);
        return reviewPage.map(this::convertToReviewDTO);
    }

    public Page<ReviewDTO> findReviewsByUid(String uid, Pageable pageable) {
        Page<ReviewDocument> reviewPage = reviewRepository.findByUid(uid, pageable);
        return reviewPage.map(this::convertToReviewDTO);
    }

    // 공통 변환 로직을 메서드로 분리
    private ReviewDTO convertToReviewDTO(ReviewDocument review) {

        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        int variantId = review.getVariantId();
        Optional<ProductVariants> optionalProductVariants = productVariantsRepository.findById(variantId);
        optionalProductVariants.ifPresent(reviewDTO::setProductVariants);

        return reviewDTO;
    }





    // 특정 리뷰 조회
    public ReviewDTO findReview(String uid) {
        Optional<ReviewDocument> optReview = reviewRepository.findByUid(uid);

        // Optional이 존재할 경우 Document를 DTO로 변환하여 반환
        return optReview.map(review -> modelMapper.map(review, ReviewDTO.class)).orElse(null);
    }

    // 리뷰 추가
    public ReviewDTO insertReview(ReviewDTO reviewDTO) {


        ReviewDocument reviewDocument = modelMapper.map(reviewDTO, ReviewDocument.class);
        ReviewDocument savedReview = reviewRepository.save(reviewDocument);

        return modelMapper.map(savedReview, ReviewDTO.class);
    }


    // 리뷰 업데이트
    public ReviewDTO updateReview(ReviewDTO reviewDTO) {
        Optional<ReviewDocument> optReview = reviewRepository.findByUid(reviewDTO.getUid());

        if (optReview.isPresent()) {
            ReviewDocument savedReview = optReview.get();

            // 필요한 필드 업데이트
            savedReview.setContent(reviewDTO.getContent());
            savedReview.setRegDate(reviewDTO.getRegDate());

            ReviewDocument modifiedReview = reviewRepository.save(savedReview);

            // 수정된 Document를 DTO로 변환하여 반환
            return modelMapper.map(modifiedReview, ReviewDTO.class);
        }

        return null;
    }

    // 리뷰 삭제
    public boolean deleteReview(String uid) {
        Optional<ReviewDocument> optReview = reviewRepository.findByUid(uid);

        if (optReview.isPresent()) {
            reviewRepository.delete(optReview.get());
            return true;
        }

        return false;
    }

    // 리뷰 사진 업로드
    public String uploadReviewImage(MultipartFile file) {

        String uploadDir = System.getProperty("user.dir") + "/uploads/review/";
        File fileUploadPath = new File(uploadDir);

        // 파일 업로드 디렉터리가 존재하지 않으면 디렉터리 생성
        if (!fileUploadPath.exists()) {
            fileUploadPath.mkdirs();
        }

        if (!file.isEmpty()) {
            String oName = file.getOriginalFilename();
            assert oName != null;
            String ext = oName.substring(oName.lastIndexOf("."));
            String sName = UUID.randomUUID().toString() + ext;

            // 파일 저장
            try {
                file.transferTo(new File(uploadDir + "review_img_" + sName));
            } catch (IOException e) {
                log.error(e);
            }
            return "review_img_" + sName;
        }
        return null;

    }
}
