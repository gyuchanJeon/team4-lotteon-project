package com.lotte4.service;

import com.lotte4.dto.*;
import com.lotte4.entity.ProductCate;
import com.lotte4.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/*
    2024/10/30 캐싱 어노테이션 추가
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CachingService cachingService;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public ProductCateDTO getProductCate(int productCateId){

        ProductCate productCate = categoryRepository.findById(productCateId).orElseThrow(NullPointerException::new);

        return modelMapper.map(productCate, ProductCateDTO.class);
    }

    public int getProductCateIdByName(String name){
        ProductCate productCate = categoryRepository.findByName(name);
        if (productCate == null) {
            return 0;
        }
        return productCate.getProductCateId();
    }


    public List<ProductCateDTO> getALLProductCate(){

        List<ProductCate> productCateList = categoryRepository.findAll();
        List<ProductCateDTO> productCateDTOList = new ArrayList<>();

        for(ProductCate productCate : productCateList){
            productCateDTOList.add(new ProductCateDTO(productCate));
        }
        return productCateDTOList;
    }

    public void insertProductCate(ProductRegisterCateDTO productCateDTO, String parent){

        cachingService.insertProductCate(productCateDTO, parent);
    }


    public List<ProductCateChildDTO> getProductCateListWithDepth(int depth){

        return cachingService.getProductCateListWithDepth(depth);
    }

    public boolean deleteProductCate(String name){

        return cachingService.deleteProductCate(name);

    }

    public boolean updateProductCateOrder(List<Map<String, Object>> changes){
        return cachingService.updateProductCateOrder(changes);
    }


}
