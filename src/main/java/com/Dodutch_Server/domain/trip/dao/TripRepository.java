package com.Dodutch_Server.domain.trip.dao;

import com.Dodutch_Server.domain.trip.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
}