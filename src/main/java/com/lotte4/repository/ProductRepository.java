package com.lotte4.repository;

import com.lotte4.entity.Product;
import com.lotte4.entity.ProductCate;
import com.lotte4.entity.SellerInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByProductCateId(ProductCate productCateId);
    List<Product> findBySellerInfoId(SellerInfo sellerInfoId);

    //home에서 사용
    List<Product> findTop8ByOrderByHitDesc();
    List<Product> findTop8ByOrderByReviewDesc();
    List<Product> findTop8ByOrderByDiscountDesc();
    List<Product> findTop8ByOrderByRatingDesc();
    List<Product> findTop8ByOrderByCreatedAtDesc();
    List<Product> findTop5ByOrderBySoldDesc();



//    // status가  0 인 상품 목록 select
//    Page<Product> findByStatus(int status, Pageable pageable);
//
//    // status가 0 이고, keyword 검색하는 메서드 (모든 필드에서 검색)
//    Page<Product> findByStatusAndSearchCategoryAndKeyword(
//            int status, String searchCategory, String keyword, Pageable pageable);
  
    // sellerInfoId 와 일치하는 상품 목록 select(관리자 상품목록을 위함)
    Page<Product> findBySellerInfoId(SellerInfo sellerInfoId, Pageable pageable);

    // name 검색
    Page<Product> findBySellerInfoIdAndNameContaining(SellerInfo sellerInfoId, String name, Pageable pageable);

    // productId 검색
    Page<Product> findBySellerInfoIdAndProductId(SellerInfo sellerInfoId, int productId, Pageable pageable);

    // company 검색
    Page<Product> findBySellerInfoIdAndCompanyContaining(SellerInfo sellerInfoId, String company, Pageable pageable);

    // name 검색
    Page<Product> findByName(String name, Pageable pageable);

    // productId 검색
    Page<Product> findByProductId(int productId, Pageable pageable);

    // company 검색
    Page<Product> findByCompany(String company, Pageable pageable);

}