package com.lotte4.dto;

import com.lotte4.entity.Product;
import com.lotte4.entity.ProductVariants;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private Integer variantId;
    private String sku;
    private String productName;
    private Integer price;
    private Integer count;
    private Integer stock;
    private String img;
    private Integer deliveryFee;
    private ProductVariantsDTO ProductVariants;
    private Product product;
    private int cartId;

    private void getPrductId (){
        this.ProductVariants.getProduct().getProductId();
    }

    private ProductVariants fromEntity(){
        return null;
    }

    public static CartItemDTO createCartItemDTO(ProductVariants variant, int count, int cartId) {
        // Product 정보 설정
        ProductDTO productDTO = OrderDirectBuyDTO.fromEntity(variant.getProduct());
        ProductVariantsDTO productVariantsDTO = new ProductVariantsDTO();
        productVariantsDTO.setVariant_id(variant.getVariant_id());
        productVariantsDTO.setProduct(productDTO);

        // CartItemDTO 설정
        CartItemDTO item = new CartItemDTO();
        item.setVariantId(variant.getVariant_id());
        item.setSku(variant.getSku());
        item.setProductName(variant.getProduct().getName());
        item.setPrice(variant.getPrice());
        item.setCount(count);
        item.setStock(variant.getStock());
        item.setImg(variant.getProduct().getImg1());
        item.setDeliveryFee(variant.getProduct().getDeliveryFee());
        item.setProductVariants(productVariantsDTO);
        item.setCartId(cartId);
        return item;
    }

}
