package com.lotte4.repository;

import com.lotte4.entity.MemberInfo;
import com.lotte4.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// 추가작업 2024-11-07 조수빈 : point를 memberId로 조회하여 가져오는 쿼리문 추가

@Repository
public interface PointRepository extends JpaRepository<Point,Integer> {

    @Query("SELECT (SUM(p.presentPoint)) FROM Point p WHERE p.memberInfo.memberInfoId = :memberInfoId")
    Integer findTotalPointsByMemberInfoId(@Param("memberInfoId") int memberInfoId);

    Page<Point> findPointsByTypeOrderByPointDateDesc(String type, Pageable pageable);

    Page<Point> findPointsByMemberInfoAndTypeOrderByPointDateDesc(MemberInfo memberInfo, String type, Pageable pageable);

    Page<Point> findPointsByMemberInfoOrderByPointDateDesc(MemberInfo memberInfo, Pageable pageable);

    Page<Point> findByTypeAndMemberInfo_EmailContainingOrderByPointDateDesc(String type, String keyword, Pageable pageable);
    Page<Point> findByTypeAndMemberInfo_NameContainingOrderByPointDateDesc(String type, String keyword, Pageable pageable);
    Page<Point> findByTypeAndMemberInfo_HpContainingOrderByPointDateDesc(String type, String keyword, Pageable pageable);

    @Query("SELECT p FROM Point p WHERE p.type = :type AND " +
            "(p.memberInfo.email LIKE %:keyword% OR " +
            "p.memberInfo.name LIKE %:keyword% OR " +
            "p.memberInfo.hp LIKE %:keyword%) " +
            "ORDER BY p.pointDate DESC")
    Page<Point> findByTypeAndAllFieldsContaining(@Param("type") String type, @Param("keyword") String keyword, Pageable pageable);

    Page<Point> findByMemberInfo_EmailContainingOrderByPointDateDesc( String keyword, Pageable pageable);
    Page<Point> findByMemberInfo_NameContainingOrderByPointDateDesc(String keyword, Pageable pageable);
    Page<Point> findByMemberInfo_HpContainingOrderByPointDateDesc(String keyword, Pageable pageable);

}
