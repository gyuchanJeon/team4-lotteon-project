package com.lotte4.repository.board;

import com.lotte4.entity.Board;
import com.lotte4.entity.BoardCate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findByTypeOrderByRegDateDesc(String type, Pageable pageable);
    Page<Board> findByTypeAndUser_UidOrderByRegDateDesc(String type, String uid, Pageable pageable);

    Page<Board> findByCate_BoardCateIdAndTypeOrderByRegDateDesc(int CateId,String type, Pageable pageable);
    Page<Board> findByCate_Parent_BoardCateIdAndTypeOrderByRegDateDesc(int parentId, String type,Pageable pageable);

    List<Board> findTop5ByTypeOrderByRegDateDesc(String type);

    @Query("SELECT COUNT(b) FROM Board b WHERE DATE(b.regDate) = :today")
    int findAllByDay(@Param("today") LocalDate day);
}
