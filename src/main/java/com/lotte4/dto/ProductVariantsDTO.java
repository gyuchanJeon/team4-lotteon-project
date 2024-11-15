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
public class ProductVariantsDTO {

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

    private LocalDateTime updated_at;

    private ProductDTO product;
    private Product product1;

    public ProductVariantsDTO(ProductVariants productVariants) {
        this.variant_id = productVariants.getVariant_id();
        this.sku = productVariants.getSku();
        this.price = productVariants.getPrice();
        this.stock = productVariants.getStock();
        this.options = productVariants.getOptions();
        this.updated_at = productVariants.getUpdated_at();

        if (productVariants.getProduct() != null) {
            this.product = new ProductDTO(productVariants.getProduct());
        }
    }
}
