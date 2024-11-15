package com.lotte4.dto;

import com.lotte4.entity.BoardCate;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCateDTO {

    private int boardCateId;
    private String name;
    private String depth;
    private BoardCateDTO parent;
    private BoardCateDTO child;



}
