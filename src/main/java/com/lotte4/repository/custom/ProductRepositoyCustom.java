package com.lotte4.repository.custom;

import com.lotte4.entity.Product;

import java.util.List;

public interface ProductRepositoyCustom {
    public List<Product> findByNameWithKeyword(String keyword);
    public List<Product> findByNameOrDescWithKeyword(String keyword);
    public List<Product> findByKeywordWithFilters(String keyword, List<String> filters, Integer minPrice, Integer maxPrice);
}
