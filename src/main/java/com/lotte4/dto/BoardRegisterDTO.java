package com.lotte4.dto;

import com.lotte4.entity.User;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRegisterDTO {
    private int boardId;
    private String type;
    // BoardCateDTO를 받아오기보다는 boardCateId를 받아와서 조회하여 set 해주기로 결정 (이유: 더 간결한 구현)
    // TODO : <select> th:value로 BoardCateDTO를 받아올 수 있는지 시도는 해보자
    private int cate;
    private String title;
    private String content;
    private String regIp;
    private int state;
    private String comment;
    // 추가필드
    private String uid; // 이 친구도 User를 안담고 int user를 담고있음

}
