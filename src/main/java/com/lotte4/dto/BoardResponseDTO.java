package com.lotte4.dto;

import com.lotte4.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDTO {
    private int boardId;
    private int rowNumber;
    private UserDTO user;
    private BoardCateDTO cate;

    // 자주 묻는 질문인지 고객센터인지
    private String type;
    // 제목
    private String title;
    // 내용
    private String content;
    private LocalDateTime regDate;
    private String regIp;
    private int state;
    private String comment;
    


}
