package com.Dodutch_Server.domain.trip.repository;

import com.Dodutch_Server.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    Optional<TripMember> findByTripIdAndMemberId(Long tripId, Long memberId);
    List<TripMember> findByTripId(Long tripId);
    @Query("SELECT tm.trip.id FROM TripMember tm WHERE tm.member.id = :memberId")
    List<Long> findTripIdsByMemberId(@Param("memberId") Long memberId);
}