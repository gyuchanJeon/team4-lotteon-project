package com.lotte4.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"parent", "children"})
@EqualsAndHashCode(exclude = {"parent", "children"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "board_Cate")
public class BoardCate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardCateId;
    private String name;
    private int depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parentId")
    private BoardCate parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<BoardCate> children = new ArrayList<>();

}
