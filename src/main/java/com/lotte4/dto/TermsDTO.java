package com.lotte4.dto;

import com.lotte4.entity.Terms;
import com.lotte4.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsDTO {

    private int termsId;

    /////이용약관////////
    // 일반회원일 경우
    private String term;
    // 판매자회원일 경우
    private String tax;

    /////전자금융 이용약관/////
    private String finance;

    /////개인정보 수집동의/////
    private String privacy;

    /////위치정보 이용약관(일반 회원만 출력)/////
    private String location;

    // type이 1일때 member, 2일때 seller 정보 들고옴
    private int type;

    public Terms toEntity() {
        return Terms.builder()
                .termsId(termsId)
                .term(term)
                .tax(tax)
                .finance(finance)
                .privacy(privacy)
                .location(location)
                .build();
    }

}
