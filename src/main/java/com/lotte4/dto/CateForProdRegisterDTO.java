package com.lotte4.dto;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CateForProdRegisterDTO {

    private int productCateId;
    private String name;
    private int depth;

}
