package com.lotte4.dto.admin.config;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitDTO {

    //채용 글 번호
    private int recruitId;
    // 채용부서
    private String division;
    // 경력사항
    private String career;
    // 제목
    private String title;
    // 작성자
    private String author;
    // 채용 형태
    private String employment;
    // 채용 시작 날짜
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate sDate;
    // 채용 종료 날짜
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate eDate;
    // 작성날짜
    private LocalDateTime regdate;
    private String sDateFormatted;
    private String eDateFormatted;
    private String regdateFormatted;
    // 비고
    private String content;
    // 채용상태
    private String status;

    public void updateStatus(){
        LocalDate now = LocalDate.now();
        if ((sDate != null && !now.isBefore(sDate)) && (eDate != null && !now.isAfter(eDate))) {
            // 현재 날짜가 sDate 이상이고 eDate 이하인 경우 "모집중"으로 설정
            this.status = "모집중";
        } else {
            // 그렇지 않으면 "마감"으로 설정
            this.status = "마감";
        }
    }
    public void formatDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.sDateFormatted = (sDate != null) ? sDate.format(formatter) : null;
        this.eDateFormatted = (eDate != null) ? eDate.format(formatter) : null;
        this.regdateFormatted = (regdate != null) ? regdate.format(dateTimeFormatter) : null;
    }
}
