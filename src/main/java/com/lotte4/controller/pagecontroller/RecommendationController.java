package com.lotte4.controller.pagecontroller;

import com.lotte4.dto.mongodb.RecommendationResult;
import com.lotte4.service.mongodb.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{productId}")
    public ResponseEntity<List<RecommendationResult>> getRecommendations(@PathVariable int productId, @RequestParam String currentUserId) {
        List<RecommendationResult> recommendations = recommendationService.getRecommendedProducts(productId, currentUserId);
        log.info("여기는 api"+recommendations);
        return ResponseEntity.ok(recommendations);
    }
}
