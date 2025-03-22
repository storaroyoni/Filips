package com.filips.healthServer.repository;

import com.filips.healthServer.model.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Integer> {

    @Query("SELECT h FROM HealthData h WHERE h.device = :device AND h.measuredAt > :sevenDaysAgo")
    List<HealthData> findRecentHealthData(@Param("device") String device, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

    @Query("SELECT h FROM HealthData h WHERE h.isPublic = :isPublic AND h.measuredAt > :sevenDaysAgo")
    List<HealthData> findPublicHealthData(@Param("isPublic") boolean isPublic, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);


}