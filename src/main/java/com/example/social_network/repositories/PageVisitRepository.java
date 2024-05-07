package com.example.social_network.repositories;

import com.example.social_network.entity.PageVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PageVisitRepository extends JpaRepository<PageVisit, Long> {
    long countByUserPageId(Long userId);

    @Query("SELECT v FROM PageVisit v WHERE v.userPageId = :userId AND v.visitTimestamp BETWEEN :startDate AND :endDate")
    List<PageVisit> findVisitByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
