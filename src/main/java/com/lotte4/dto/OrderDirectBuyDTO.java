package com.lotte4.dto;

import com.lotte4.entity.Product;
import com.lotte4.entity.ProductVariants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDirectBuyDTO {

    private int variant_id;


    @NotBlank(message = "SKU는 필수 항목입니다.")
    private String sku;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private int stock;

    @NotNull(message = "옵션은 필수 항목입니다.")
    @NotEmpty(message = "옵션은 비어 있을 수 없습니다.")
    private Map<List<String>, List<String>> options;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    private Product product;
    private ProductVariants productVariants;
    private int count;

    private Map<ProductVariants, Integer> variantCountMap;

    // getters 및 setters
    public Map<ProductVariants, Integer> getVariantCountMap() {
        return variantCountMap;
    }

    public void setVariantCountMap(Map<ProductVariants, Integer> variantCountMap) {
        this.variantCountMap = variantCountMap;
    }

    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setImg1(product.getImg1());
        productDTO.setDeliveryFee(product.getDeliveryFee());
        productDTO.setDiscount(product.getDiscount());
//        productDTO.getSellerInfoId(product.getSellerInfoId());

        return productDTO;
    }

}
