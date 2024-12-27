package com.Dodutch_Server.domain.expense.repository;

import com.Dodutch_Server.domain.expense.entity.ExpenseMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseMemberRepository extends JpaRepository<ExpenseMember, Long> {

    List<ExpenseMember> findByExpenseId(Long expenseId);
    void deleteByExpenseId(Long expenseId);
}