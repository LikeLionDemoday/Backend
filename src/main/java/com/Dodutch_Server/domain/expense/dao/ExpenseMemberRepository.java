package com.Dodutch_Server.domain.expense.dao;

import com.Dodutch_Server.domain.expense.domain.ExpenseMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseMemberRepository extends JpaRepository<ExpenseMember, Long> {
}