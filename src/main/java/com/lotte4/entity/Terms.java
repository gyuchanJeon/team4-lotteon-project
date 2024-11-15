package com.lotte4.entity;

import com.lotte4.dto.TermsDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "terms")
public class Terms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int termsId;


    /////이용약관////////
    // 일반회원일 경우
    @Column(columnDefinition = "TEXT", nullable = false)

    private String term;
    // 판매자회원일 경우
    @Column(columnDefinition = "TEXT", nullable = false)
    private String tax;

    /////전자금융 이용약관/////
    @Column(columnDefinition = "TEXT", nullable = false)
    private String finance;

    /////개인정보 수집동의/////
    @Column(columnDefinition = "TEXT", nullable = false)
    private String privacy;

    /////위치정보 이용약관(일반 회원만 출력)/////
    @Column(columnDefinition = "TEXT", nullable = false)
    private String location;


//
//    public TermsDTO toDTO() {
//        return TermsDTO.builder()
//                .term(term)
//                .tax(tax)
//                .finance(finance)
//                .privacy(privacy)
//                .location(location)
//                .type(type)
//                .build();
//    }


}
