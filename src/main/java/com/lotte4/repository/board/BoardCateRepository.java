package com.lotte4.repository.board;

import com.lotte4.entity.BoardCate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//import java.util.List;

@Repository
public interface BoardCateRepository extends JpaRepository<BoardCate, Integer> {
    List<BoardCate> findBoardCateByDepth(int depth);
    @Query("SELECT b FROM BoardCate b WHERE b.parent.boardCateId = :parentId")
    List<BoardCate> findByParentId(@Param("parentId") int parentId);
    BoardCate findByBoardCateId(int boardCateId);


}
