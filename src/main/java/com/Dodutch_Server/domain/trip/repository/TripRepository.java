package com.Dodutch_Server.domain.trip.repository;

import com.Dodutch_Server.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query("SELECT t FROM Trip t WHERE (:name IS NULL OR t.name LIKE %:name%) " +
            "AND (:year IS NULL OR EXTRACT(YEAR FROM t.startDate) = :year OR EXTRACT(YEAR FROM t.endDate) = :year) " +
            "AND (:tripIds IS NULL OR t.id IN :tripIds)")
    List<Trip> searchTrips(
            @Param("name") String name,
            @Param("year") Integer year,
            @Param("tripIds") List<Long> tripIds);
    Optional<Trip> findByJoinCode(String joinCode);

    boolean existsByJoinCode(String joinCode);


}