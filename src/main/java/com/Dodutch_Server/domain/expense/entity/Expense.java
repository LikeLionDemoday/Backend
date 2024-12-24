package com.Dodutch_Server.domain.expense.entity;

import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.global.common.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Expense extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Integer cost;
    private String category;
    private LocalDate expenseDate;

    @ManyToOne
    @JoinColumn(name = "payer", nullable = true)
    private Member payer;
}
