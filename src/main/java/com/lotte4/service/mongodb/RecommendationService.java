package com.lotte4.service.mongodb;


import com.lotte4.dto.mongodb.RecommendationResult;
import com.lotte4.repository.mongodb.UserBehaviorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendationService {

    private final UserBehaviorRepository userBehaviorRepository;

    public List<RecommendationResult> getRecommendedProducts(int productId, String currentUserId) {
        return userBehaviorRepository.findRelatedProducts(productId, currentUserId);
    }
}