package com.Dodutch_Server.domain.uuid.repository;

import com.Dodutch_Server.domain.uuid.entity.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UuidRepository extends JpaRepository<Uuid, Long> {
}

