package com.Dodutch_Server.domain.dutch.service;

import com.Dodutch_Server.domain.dutch.dto.DutchDTO;
import com.Dodutch_Server.domain.dutch.entity.Dutch;
import com.Dodutch_Server.domain.dutch.repository.DutchRepository;
import com.Dodutch_Server.domain.expense.entity.Expense;
import com.Dodutch_Server.domain.expense.entity.ExpenseMember;
import com.Dodutch_Server.domain.expense.repository.ExpenseRepository;
import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.domain.trip.repository.TripMemberRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class DutchService {
    private final ExpenseRepository expenseRepository;
    private final TripMemberRepository tripMemberRepository;
    private final DutchRepository dutchRepository;

    public List<DutchDTO> calculateDutch(Long tripId) {
        List<Expense> expenseList = expenseRepository.findByTripId(tripId);
        List<TripMember> tripMemberList = tripMemberRepository.findByTripId(tripId);

        Map<Long, Integer> memberSpent = new HashMap<>();
        Integer totalSpent = 0;

        for (Expense expense : expenseList) {
            for (TripMember member : expense.getMembers()) {
                Integer amountSpent = expense.getAmountSpentByMember(member.getMember().getId());
                memberSpent.put(member.getMember().getId(), memberSpent.getOrDefault(member.getMember().getId(), 0) + amountSpent);
                totalSpent += amountSpent;
            }
        }

        Integer averageSpent = totalSpent / expenseList.size();

        Map<Long, Integer> memberBalance = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : memberSpent.entrySet()) {
            Integer balance = entry.getValue() - averageSpent;
            memberBalance.put(entry.getKey(), balance);
        }

        List<DutchDTO> dutchList = calculatePerCost(memberBalance, tripId, tripMemberList);
        saveDutch(dutchList);
        return dutchList;
    }

    private void saveDutch(List<DutchDTO> dutchList) {
        for (DutchDTO dutchDTO : dutchList) {
            Dutch dutch = new Dutch();
            dutch.setPerCost(dutchDTO.getPerCost());
            dutch.setIsCompleted(dutchDTO.getIsCompleted());
            dutch.setTrip(dutchDTO.getTrip());
            dutch.setTripMember(dutchDTO.getTripMember());
            dutch.setPayer(dutchDTO.getPayer() != null ? findMemberById(dutchDTO.getPayer()) : null);
            dutch.setPayee(dutchDTO.getPayee() != null ? findMemberById(dutchDTO.getPayee()) : null);

            dutchRepository.save(dutch);
        }
    }

    private Member findMemberById(Long memberId) {
        return tripMemberRepository.findById(memberId)
                .map(TripMember::getMember)
                .orElseThrow(() -> new RuntimeException("해당 멤버를 찾을 수 없습니다."));
    }



    private List<DutchDTO> calculatePerCost(Map<Long, Integer> memberBalance, Long tripId, List<TripMember> tripMembers) {
        Integer totalAmount = 0;
        for (Integer balance : memberBalance.values()) {
            totalAmount += balance;
        }

        List<DutchDTO> dutchList = new ArrayList<>();

        Integer averageAmount = totalAmount / memberBalance.size();
        for (Map.Entry<Long, Integer> entry : memberBalance.entrySet()) {
            Long memberId = entry.getKey();
            Integer balance = entry.getValue();

            Integer perCost = balance - averageAmount;

            TripMember tripMember = tripMembers.stream()
                    .filter(tm -> tm.getId().equals(memberId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("TripMember가 없습니다."));

            Member payee = null;
            Member payer = null;

            if (perCost > 0) {
                payer = tripMember.getMember();
            } else if (perCost < 0) {
                payee = tripMember.getMember();
            } else {
                continue;
            }

            DutchDTO dto = new DutchDTO(
                    null,
                    Math.abs(perCost),
                    false,
                    tripMember.getTrip(),
                    tripMember,
                    payer,
                    payee
            );

            dutchList.add(dto);
        }
        return dutchList;
    }
}
