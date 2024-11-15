package com.lotte4.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

/*

    2024.11.06 강중원 - 방문자 통계용 엔티티 생성

 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "visitor_count")
public class VisitorCount {
    @Id
    private String id;
    private String date;
    private Integer count;

    public VisitorCount(String date, Integer count) {
        this.date = date;
        this.count = count;
    }


}
