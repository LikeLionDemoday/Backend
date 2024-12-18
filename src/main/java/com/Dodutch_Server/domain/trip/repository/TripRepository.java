package com.Dodutch_Server.domain.trip.repository;

import com.Dodutch_Server.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import com.Dodutch_Server.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByNameContaining(String name);
    List<Trip> findByNameContainingAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String name, LocalDate startDate, LocalDate endDate);
    List<Trip> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
}