package com.Dodutch_Server.domain.trip.dao;

import com.Dodutch_Server.domain.trip.domain.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
}