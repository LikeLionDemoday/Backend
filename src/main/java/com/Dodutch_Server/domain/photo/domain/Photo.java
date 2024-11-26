package com.Dodutch_Server.domain.photo.domain;

import com.Dodutch_Server.domain.expense.domain.Expense;
import jakarta.persistence.*;

@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long photoUrl;

    @ManyToOne
    @JoinColumn(name = "expenseId", nullable = false)
    private Expense expense;
}
