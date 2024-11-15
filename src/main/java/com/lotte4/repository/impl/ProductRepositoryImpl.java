package com.lotte4.repository.impl;

import com.lotte4.entity.Product;
import com.lotte4.entity.QProduct;
import com.lotte4.repository.custom.ProductRepositoyCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;

/*

    2024.11.05 강중원 - 클래스 추가

 */
@Log4j2
@AllArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepositoyCustom {
    private final JPAQueryFactory queryFactory;
    private final QProduct qProduct = QProduct.product;

    @Override
    public List<Product> findByNameWithKeyword(String keyword){
        List<Product> result = queryFactory.select(qProduct)
                .from(qProduct)
                .where(qProduct.name.contains(keyword))
                .fetch();
        return result;
    }

    @Override
    public List<Product> findByNameOrDescWithKeyword(String keyword){
        List<Product> result = queryFactory.select(qProduct)
                .from(qProduct)
                .where(qProduct.name.contains(keyword)
                .or(qProduct.description.contains(keyword)))
                .fetch();
        return result;
    }

    public List<Product> findByKeywordWithFilters(String keyword, List<String> filters, Integer minPrice, Integer maxPrice) {
        BooleanBuilder builder = new BooleanBuilder();

        // 필터 조건 추가
        if (filters != null && !filters.isEmpty()) {
            BooleanBuilder filterBuilder = new BooleanBuilder();

            // 키워드를 공백으로 분리
            String[] keywords = keyword.split("\\s+");
            for (String k : keywords) {
                for (String filter : filters) {
                    switch (filter) {
                        case "prodName":
                            filterBuilder.or(qProduct.name.contains(k)); // 각 단어에 대해 or 조건 추가
                            break;
                        case "description":
                            filterBuilder.or(qProduct.description.contains(k)); // 각 단어에 대해 or 조건 추가
                            break;
                        case "price":
                            // 할인 적용 가격 계산
                            NumberExpression<Integer> discountedPrice = qProduct.price
                                    .multiply(1.0).subtract(qProduct.price.multiply(qProduct.discount).divide(100.0));

                            // 가격 필터가 활성화된 경우, 별도로 가격 범위 조건 추가
                            if (minPrice != null) {
                                builder.and(discountedPrice.goe(minPrice.doubleValue()));
                            }
                            if (maxPrice != null) {
                                builder.and(discountedPrice.loe(maxPrice.doubleValue()));
                            }
                            break;
                    }
                }
            }
            // 필터 조건을 최종 빌더에 추가
            builder.and(filterBuilder);
        } else {
            // 필터가 없으면 기본적으로 이름 검색만 수행
            builder.and(qProduct.name.contains(keyword));
        }

        // QueryDSL 쿼리 실행
        return queryFactory.select(qProduct)
                .from(qProduct)
                .where(builder)
                .fetch();

    }
}
