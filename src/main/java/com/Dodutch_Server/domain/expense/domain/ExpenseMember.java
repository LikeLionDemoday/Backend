package com.Dodutch_Server.domain.expense.domain;

import com.Dodutch_Server.domain.trip.domain.TripMember;
import jakarta.persistence.*;

@Entity
public class ExpenseMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer shareAmount;

    @ManyToOne
    @JoinColumn(name = "tripMemberId", nullable = false)
    private TripMember tripMember;

    @ManyToOne
    @JoinColumn(name = "expenseId", nullable = false)
    private Expense expense;
}
