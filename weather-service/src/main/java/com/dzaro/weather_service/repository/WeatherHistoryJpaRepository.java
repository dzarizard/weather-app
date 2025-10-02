package com.dzaro.weather_service.repository;

import com.dzaro.weather_service.entity.WeatherHistoryEntity;
import com.dzaro.weather_service.model.HistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface WeatherHistoryJpaRepository extends JpaRepository<WeatherHistoryEntity, Long> {

    @Query("SELECT h FROM WeatherHistoryEntity h WHERE " +
           "(:city IS NULL OR h.city = :city) AND " +
           "(:fromDate IS NULL OR h.queryDate >= :fromDate) AND " +
           "(:toDate IS NULL OR h.queryDate <= :toDate) " +
           "ORDER BY h.queryDate DESC")
    List<WeatherHistoryEntity> findByCityAndDateRange(
            @Param("city") String city,
            @Param("fromDate") OffsetDateTime fromDate,
            @Param("toDate") OffsetDateTime toDate
    );

    List<WeatherHistoryEntity> findAllByOrderByQueryDateDesc();
}