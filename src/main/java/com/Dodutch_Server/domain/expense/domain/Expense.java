package com.Dodutch_Server.domain.expense.domain;

import com.Dodutch_Server.domain.member.domain.Member;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long content;
    private Integer cost;
    private Long category;
    private LocalDate expenseDate;

    @ManyToOne
    @JoinColumn(name = "payer", nullable = true)
    private Member payer;
}
