package com.Dodutch_Server.domain.dutch.service;

import com.Dodutch_Server.domain.dutch.dto.DutchDTO;
import com.Dodutch_Server.domain.dutch.entity.Dutch;
import com.Dodutch_Server.domain.dutch.repository.DutchRepository;
import com.Dodutch_Server.domain.expense.entity.Expense;
import com.Dodutch_Server.domain.expense.entity.ExpenseMember;
import com.Dodutch_Server.domain.expense.repository.ExpenseMemberRepository;
import com.Dodutch_Server.domain.expense.repository.ExpenseRepository;
import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.member.repository.MemberRepository;
import com.Dodutch_Server.domain.trip.entity.Trip;
import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.domain.trip.repository.TripMemberRepository;
import com.Dodutch_Server.domain.trip.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DutchService {

    private final DutchRepository dutchRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseMemberRepository expenseMemberRepository;
    private final MemberRepository memberRepository;

    public List<DutchDTO> calculateSettlement(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("TripId not found"));

        // 여행에 참여한 모든 멤버 가져오기
        List<TripMember> tripMembers = tripMemberRepository.findByTripId(tripId);

        // 해당 여행에 대한 모든 Expense 내역 가져오기
        List<Expense> expenses = expenseRepository.findByTripId(tripId);

        // 각 멤버가 지불한 금액을 기록할 Map
        Map<Member, Integer> memberPayments = new HashMap<>();
        Integer totalPaidAmount = 0;

        // 각 Expense에 대해 멤버가 사용한 금액을 기록할 Map
        Map<Member, Integer> perCosts = new HashMap<>();

        // 각 Expense 내역에 대해 멤버별 사용 금액 기록하기
        for (Expense expense : expenses) {
            Member payer = expense.getPayer();
            Integer totalAmount = expense.getAmount();

            memberPayments.putIfAbsent(payer, 0);
            memberPayments.put(payer, memberPayments.get(payer) + totalAmount);

            List<ExpenseMember> expenseMembers = expenseMemberRepository.findByExpenseId(expense.getId());
            // 해당 Expense 내역에 대해 각 멤버가 사용한 금액 기록하기
            for (ExpenseMember expenseMember : expenseMembers) {
                Member member = expenseMember.getTripMember().getMember();
                Integer shareAmount = expenseMember.getShareAmount();

                perCosts.putIfAbsent(member, 0);
                perCosts.put(member, perCosts.get(member) + shareAmount);
            }
        }

        // 결제 내역에 따른 차액 계산
        Map<Member, Integer> memberBalances = new HashMap<>();

        // 각 멤버의 차액 계산
        for (Map.Entry<Member, Integer> entry : perCosts.entrySet()) {
            Member member = entry.getKey();
            Integer totalPaid = memberPayments.getOrDefault(member, 0); // 실제 결제 금액
            Integer totalSpent = entry.getValue(); // 사용 금액
            int amountOwed = totalPaid - totalSpent; // 결제 금액과 사용 금액의 차이

            // 차액 계산하기 (결제 금액에서 사용한 금액을 빼면 그 금액을 지불해야 하는지 혹은 받을 수 있는지 계산)
            memberBalances.put(member, amountOwed);
        }

        // 정산 결과 저장을 위한 리스트
        List<DutchDTO> dutchDTOs = new ArrayList<>();

        // 돈을 받아야 하는 멤버(양수)와 돈을 줘야 하는 멤버(음수) 나누기
        List<Map.Entry<Member, Integer>> positiveBalances = new ArrayList<>();
        List<Map.Entry<Member, Integer>> negativeBalances = new ArrayList<>();

        for (Map.Entry<Member, Integer> entry : memberBalances.entrySet()) {
            if (entry.getValue() > 0) {
                positiveBalances.add(entry);
            } else if (entry.getValue() < 0) {
                negativeBalances.add(entry);
            }
        }

        // 정산 계산
        for (Map.Entry<Member, Integer> positiveEntry : positiveBalances) {
            Member payee = positiveEntry.getKey();
            Integer payeeBalance = positiveEntry.getValue();

            for (Map.Entry<Member, Integer> negativeEntry : negativeBalances) {
                if (payeeBalance <= 0) break;

                Member payer = negativeEntry.getKey();
                Integer payerBalance = negativeEntry.getValue();

                if (payerBalance < 0) {
                    Integer amountToPay = Math.min(payeeBalance, Math.abs(payerBalance));

                    // DTO 추가
                    dutchDTOs.add(new DutchDTO(payer.getId(), payee.getId(), amountToPay));

                    // 잔액 갱신
                    payeeBalance -= amountToPay;
                    payerBalance += amountToPay;

                    memberBalances.put(payee, payeeBalance);
                    memberBalances.put(payer, payerBalance);
                }
            }
        }
        return dutchDTOs;
    }

    @Transactional
    public void saveSettlementToDatabase(Long tripId, List<DutchDTO> dutchDTOs) {
        // 여행 프로젝트의 기존 정산 내역 삭제
        dutchRepository.deleteByTripId(tripId);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("TripId not found"));

        for (DutchDTO dutchDTO : dutchDTOs) {
            Member payer = memberRepository.findById(dutchDTO.getPayer())
                    .orElseThrow(() -> new IllegalArgumentException("Payer not found"));
            Member payee = memberRepository.findById(dutchDTO.getPayee())
                    .orElseThrow(() -> new IllegalArgumentException("Payee not found"));

            // TripMember 확인
            TripMember tripMember = tripMemberRepository.findByTripIdAndMemberId(trip.getId(), payer.getId())
                    .orElseThrow(() -> new IllegalArgumentException("TripMember not found for payer"));

            // Dutch Entity 생성 및 저장
            Dutch dutch = new Dutch();
            dutch.setTrip(trip);
            dutch.setTripMember(tripMember);
            dutch.setPayer(payer);
            dutch.setPayee(payee);
            dutch.setPerCost(dutchDTO.getAmountToPay());
            dutch.setIsCompleted(false);
            dutchRepository.save(dutch);

        }
    }
}