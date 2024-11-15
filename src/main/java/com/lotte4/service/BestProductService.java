package com.lotte4.service;

import com.lotte4.dto.ProductBestDTO;
import com.lotte4.dto.ProductListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class BestProductService {
    private final RedisTemplate redisTemplate;

    public void updateSalesInRedis(ProductBestDTO productBestDTO, int sold) {
        String key = "ProductBest:" + productBestDTO.getProductId(); // Redis 키 생성
        if (redisTemplate.hasKey(key)) {

            // Redis에 업데이트된 판매량 저장
            redisTemplate.opsForHash().put(key, "productId", productBestDTO.getProductId());
            redisTemplate.opsForHash().put(key, "name", productBestDTO.getName());
            redisTemplate.opsForHash().put(key, "price", productBestDTO.getPrice());
            redisTemplate.opsForHash().put(key, "discount", productBestDTO.getDiscount());
            redisTemplate.opsForHash().put(key, "img1", productBestDTO.getImg1());


            // Redis에 기존 판매량이 있는 경우
            Integer currentSold = (Integer) redisTemplate.opsForHash().get(key, "sold");

            redisTemplate.opsForHash().put(key, "sold", currentSold+sold);

            // Redis에 기존 판매량이 있는 경우
            Integer changedSold = (Integer) redisTemplate.opsForHash().get(key, "sold");

            // ZSet에 판매량 업데이트
            redisTemplate.opsForZSet().add("best_selling_products", key, changedSold);
        } else {
            // Redis에 키가 없으면, 현재 ProductBestDTO 정보를 저장
            Map<String, Object> productData = new HashMap<>();
            productData.put("productId", productBestDTO.getProductId());
            productData.put("name", productBestDTO.getName());
            productData.put("price", productBestDTO.getPrice());
            productData.put("discount", productBestDTO.getDiscount());
            productData.put("sold", sold);
            productData.put("img1", productBestDTO.getImg1());

            // Redis에 해당 데이터 저장
            redisTemplate.opsForHash().putAll(key, productData);

            // ZSet에 추가하여 순위 관리
            redisTemplate.opsForZSet().add("best_selling_products", key, sold);
        }
    }

    // 상위 5개 제품 가져오기 메서드
    public List<ProductBestDTO> getTop5BestSelling() {
        // ZSet에서 상위 5개 키를 가져옴
        Set<String> top5Keys = redisTemplate.opsForZSet().reverseRange("best_selling_products", 0, 4);

        List<ProductBestDTO> top5Products = new ArrayList<>();
        if (top5Keys != null) {
            for (String key : top5Keys) {
                Map<Object, Object> productData = redisTemplate.opsForHash().entries(key);
                ProductBestDTO product = mapToProductBestDTO(productData);
                if (product != null) {
                    top5Products.add(product);
                }
            }
        }
        return top5Products;
    }




    // Map 데이터를 ProductBestDTO로 변환하는 메서드
    private ProductBestDTO mapToProductBestDTO(Map<Object, Object> productData) {
        if (productData.isEmpty()) return null;

        return ProductBestDTO.builder()
                .productId((Integer) productData.get("productId"))
                .name((String) productData.get("name"))
                .price((Integer) productData.get("price"))
                .discount((Integer) productData.get("discount"))
                .img1((String) productData.get("img1"))
                .build();
    }
}
