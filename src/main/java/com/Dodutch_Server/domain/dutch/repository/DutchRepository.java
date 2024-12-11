package com.Dodutch_Server.domain.dutch.repository;

import com.Dodutch_Server.domain.dutch.entity.Dutch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DutchRepository extends JpaRepository<Dutch, Long> {
}