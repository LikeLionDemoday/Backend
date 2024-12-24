package com.Dodutch_Server.domain.trip.repository;

import com.Dodutch_Server.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
}