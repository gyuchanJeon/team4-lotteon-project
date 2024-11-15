package com.lotte4.repository;

import com.lotte4.entity.ProductVariants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantsRepository extends JpaRepository<ProductVariants, Integer> {
    @Query("SELECT pv FROM ProductVariants pv WHERE pv.variant_id IN :ids")
    List<ProductVariants> findByVariantIdIn(@Param("ids") List<Integer> ids);

    @Query("SELECT pv FROM ProductVariants pv LEFT JOIN Product p on pv.product.productId = p.productId")
    List<ProductVariants> findAllProductVariants();
}
