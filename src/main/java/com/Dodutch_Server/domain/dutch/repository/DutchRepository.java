package com.Dodutch_Server.domain.dutch.repository;

import com.Dodutch_Server.domain.dutch.entity.Dutch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DutchRepository extends JpaRepository<Dutch, Long> {
    List<Dutch> findByTripId(Long tripId);
    Optional<Dutch> findByTripIdAndId(Long tripId, Long id);
    boolean existsByTripIdAndPayerIdAndPayeeId(Long tripId, Long payerId, Long payeeId);
    List<Dutch> findByPayerIdOrPayeeId(Long payerId, Long payeeId);
    List<Dutch> findByPayerIdOrPayeeIdOrderByCreatedAtDesc(Long payerId, Long payeeId);
    void deleteByTripId(Long tripId);
}
