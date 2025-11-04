package com.dzaro.weather_service.repository;

import com.dzaro.weather_service.entity.WeatherRequestHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface WeatherHistoryRequestRepository extends JpaRepository<WeatherRequestHistoryEntity, Long>,
JpaSpecificationExecutor<WeatherRequestHistoryEntity>  {

    @Query("""
            SELECT h FROM WeatherRequestHistoryEntity h
            WHERE (:city IS NULL OR h.city = :city)
              AND h.queryDate BETWEEN :fromTs AND :toTs
            ORDER BY h.queryDate DESC
    """)
    List<WeatherRequestHistoryEntity> findByCityAndDateRange(
            @Param("city") String city,
            @Param("fromTs") OffsetDateTime fromTs,
            @Param("toTs") OffsetDateTime toTs
    );
}