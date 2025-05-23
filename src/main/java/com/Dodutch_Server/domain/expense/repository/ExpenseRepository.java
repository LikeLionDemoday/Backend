package com.Dodutch_Server.domain.expense.repository;

import com.Dodutch_Server.domain.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT new map(e.expenseCategory as category, COALESCE(SUM(e.amount), 0) as amount) " +
            "FROM Expense e WHERE e.trip.id = :tripId GROUP BY e.expenseCategory")
    List<Map<String, Object>> findCategoryCostsByTripId(@Param("tripId") Long tripId);
    List<Expense> findByTripIdOrderByExpenseDateAsc(Long tripId);
    List<Expense> findByTripId(Long tripId);
}