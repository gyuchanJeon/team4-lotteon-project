package com.lotte4.dto;

import com.lotte4.entity.ProductVariants;
import com.lotte4.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.List;

/*
     날짜 : 2024/10/30
     이름 : 강은경
     내용 : CartResponseDTO 생성

     수정이력
      - 2024/10/30 강은경 - cart Response용 dto 생성
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {

    private User user;

    private List<Integer> productVariants;

    // 갯수
    private List<Integer> counts;



}
