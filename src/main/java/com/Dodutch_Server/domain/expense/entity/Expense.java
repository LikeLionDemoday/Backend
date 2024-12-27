package com.Dodutch_Server.domain.expense.entity;

import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.trip.entity.Trip;
import com.Dodutch_Server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    //추가
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false) // 외래 키 설정
    private Trip trip;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ExpenseMember> expenseMembers = new ArrayList<>();
}
