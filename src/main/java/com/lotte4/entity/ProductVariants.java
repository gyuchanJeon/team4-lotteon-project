package com.lotte4.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lotte4.config.MapStringListToJsonConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product_variants")
public class ProductVariants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int variant_id;

    private String sku; // 제품 고유 네이밍(ex 검은색 S 티셔츠 = GTXEM-101 / 검은색 M 티셔츠 = GTXEM-102)
    private int price;
    private int stock;

    @Convert(converter = MapStringListToJsonConverter.class)
    private Map<List<String>, List<String>> options; // sku에 대한 옵션(ex 검은색 S / 파란색 L)

    private LocalDateTime updated_at;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;


}
