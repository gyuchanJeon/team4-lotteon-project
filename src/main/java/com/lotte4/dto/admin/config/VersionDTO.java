package com.lotte4.dto.admin.config;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VersionDTO {
    private int versionId;
    // 버전 이름
    private String versionName;
    // 내용
    private String content;
    // 작성한 시간
    private LocalDateTime regDate;
    // 작성자
    private String uid;
    // TODO : 나중에는 User로 변경
}
