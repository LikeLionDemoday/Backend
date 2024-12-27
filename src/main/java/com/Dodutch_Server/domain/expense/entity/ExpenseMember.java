package com.Dodutch_Server.domain.expense.entity;

import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ExpenseMember extends BaseEntity {
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
