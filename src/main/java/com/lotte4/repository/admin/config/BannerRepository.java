package com.lotte4.repository.admin.config;

import com.lotte4.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {
    public List<Banner> findByLocation(String location);
    public List<Banner> findByLocationAndState(String location, int status);

}
