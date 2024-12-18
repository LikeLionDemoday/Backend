package com.Dodutch_Server.domain.photo.entity;

import com.Dodutch_Server.domain.expense.entity.Expense;
import com.Dodutch_Server.global.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Photo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "expenseId", nullable = false)
    private Expense expense;
}
