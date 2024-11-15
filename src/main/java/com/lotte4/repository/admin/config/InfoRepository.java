package com.lotte4.repository.admin.config;

import com.lotte4.entity.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoRepository extends JpaRepository<Info,Integer> {

    @Modifying
    @Query("UPDATE Info i SET i.title = :title, i.subTitle = :subtitle WHERE i.infoId = 1")
    int updateTitleAndSubtitle(@Param("title") String title, @Param("subtitle") String subtitle);

    @Modifying
    @Query("UPDATE Info i SET i.companyName = :companyName, i.companyCeo = :companyCeo, " +
            "i.companyBusinessNumber = :companyBusinessNumber, i.mosaNumber = :mosaNumber, " +
            "i.companyAddress = :companyAddress, i.companyAddress2 = :companyAddress2 WHERE i.infoId = 1")
    int updateCompanyInfo(
            @Param("companyName") String companyName,
            @Param("companyCeo") String companyCeo,
            @Param("companyBusinessNumber") String companyBusinessNumber,
            @Param("mosaNumber") String mosaNumber,
            @Param("companyAddress") String companyAddress,
            @Param("companyAddress2") String companyAddress2
    );

    @Modifying
    @Query("UPDATE Info i SET i.csHp = :csHp, i.csWorkingHours = :csWorkingHours, " +
            "i.csEmail = :csEmail, i.consumer = :consumer " +
            "WHERE i.infoId = 1")
    int updateCompanyCs(
            @Param("csHp") String csHp,
            @Param("csWorkingHours") String csWorkingHours,
            @Param("csEmail") String csEmail,
            @Param("consumer") String consumer
    );

    @Modifying
    @Query("UPDATE Info i SET i.copyright = :copyright WHERE i.infoId = 1")
    int updateCopyright(@Param("copyright") String copyright);
}

