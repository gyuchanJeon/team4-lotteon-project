package com.lotte4.repository;

import com.lotte4.entity.ProductCate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCate, Integer> {
    public List<ProductCate> findByDepth(Integer depth);
    public ProductCate findByName(String name);
}
