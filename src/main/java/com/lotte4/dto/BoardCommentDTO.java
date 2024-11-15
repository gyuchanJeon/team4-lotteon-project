package com.lotte4.dto;

import lombok.*;

@Getter
@Setter
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCommentDTO {

    private int boardId;
    private String comment;


}
