package com.lotte4.repository.admin.config;

import com.lotte4.entity.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecruitRepository extends JpaRepository<Recruit, Integer>, JpaSpecificationExecutor<Recruit> {
    @Query("SELECT r FROM Recruit r WHERE r.eDate < :date AND r.status = :status")
    List<Recruit> findExpiredRecruits(@Param("date") LocalDate date, @Param("status") String status);

    void deleteAllByRecruitIdIn(List<Integer> recruitIds);
}
