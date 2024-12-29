package com.Dodutch_Server.domain.expense.service;

import com.Dodutch_Server.domain.expense.dto.ExpenseRequestDTO;
import com.Dodutch_Server.domain.expense.dto.ExpenseResponseDTO;
import com.Dodutch_Server.domain.expense.entity.Expense;
import com.Dodutch_Server.domain.expense.entity.ExpenseMember;
import com.Dodutch_Server.domain.expense.repository.ExpenseRepository;
import com.Dodutch_Server.domain.expense.repository.ExpenseMemberRepository;
import com.Dodutch_Server.domain.trip.entity.Trip;
import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.member.repository.MemberRepository;
import com.Dodutch_Server.domain.trip.repository.TripMemberRepository;
import com.Dodutch_Server.domain.trip.repository.TripRepository;
import com.Dodutch_Server.domain.uuid.entity.Uuid;
import com.Dodutch_Server.domain.uuid.repository.UuidRepository;
import com.Dodutch_Server.global.common.apiPayload.code.status.ErrorStatus;
import com.Dodutch_Server.global.common.exception.handler.ErrorHandler;
import com.Dodutch_Server.global.config.aws.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMemberRepository expenseMemberRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TripRepository tripRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    @Transactional
    public void addExpense(Long tripId, Long payerId,ExpenseRequestDTO request, MultipartFile expenseImage) {
        // tripId로 TripMember 테이블에서 멤버 조회
        List<TripMember> tripMembers = tripMemberRepository.findByTripId(tripId);

        // TripMember의 memberId 리스트 추출
        List<Long> requiredMemberIds = tripMembers.stream()
                .map(tripMember -> tripMember.getMember().getId())
                .toList();

        // Request의 members 리스트에서 memberId 추출
        List<Long> providedMemberIds = request.getMembers().stream()
                .map(ExpenseRequestDTO.MemberShareDTO::getMemberId)
                .toList();

        // 모든 멤버가 제공되었는지 검증
        if (!requiredMemberIds.containsAll(providedMemberIds) || !providedMemberIds.containsAll(requiredMemberIds)) {
            throw new IllegalArgumentException("모든 사람의 값을 입력해라");
        }

        // amount 값과 members의 cost 합 검증
        int totalCost = request.getMembers().stream()
                .mapToInt(ExpenseRequestDTO.MemberShareDTO::getCost)
                .sum();

        if (!(request.getAmount()==totalCost)) {
            throw new IllegalArgumentException("총합이 일치하지 않습니다");
        }

        // 결제자(Member) 조회
        Member payer = memberRepository.findById(payerId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // Trip 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));

        Uuid ExpenseUuid = uuidRepository.save(Uuid.builder().uuid(UUID.randomUUID().toString()).build());
        String expenseImageUrl = s3Manager.uploadFile(s3Manager.generateExpenseKeyName(ExpenseUuid), expenseImage);

        // Expense 생성 및 저장
        Expense expense = Expense.builder()
                .title(request.getTitle())
                .expenseCategory(request.getExpenseCategory())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .memo(request.getMemo())
                //.payer(payer)
                .trip(trip)
                .expenseImageUrl(expenseImageUrl)
                .build();

        expenseRepository.save(expense);

        // Trip의 totalCost 업데이트
        updateTotalCost(trip, expense.getAmount());

        // ExpenseMember 생성 및 저장
        for (ExpenseRequestDTO.MemberShareDTO memberDTO : request.getMembers()) {
            TripMember tripMember = tripMemberRepository.findByTripIdAndMemberId(tripId, memberDTO.getMemberId())
                    .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_MEMBER_EXIST));

            ExpenseMember expenseMember = ExpenseMember.builder()
                    .shareAmount(memberDTO.getCost())
                    .tripMember(tripMember)
                    .expense(expense)
                    .build();

            expenseMemberRepository.save(expenseMember);
        }
    }

//    @Transactional
//    public void updateExpense(Long tripId, Long expenseId, ExpenseRequestDTO request) {
//        Expense expense = expenseRepository.findById(expenseId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 지출이 존재하지 않습니다: " + expenseId));
//
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다: " + tripId));
//
//        updateTotalCost(trip, expense.getCost());
//
//        expense.setContent(request.getTitle());
//        expense.setCategory(request.getCategory());
//        expense.setCost(request.getAmount());
//        expense.setExpenseDate(request.getDate());
//
//        Member payer = memberRepository.findById(request.getMemberId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다: " + request.getMemberId()));
//        expense.setPayer(payer);
//
//        updateTotalCost(trip, expense.getCost());
//
//        List<ExpenseMember> expenseMembers = expenseMemberRepository.findByExpenseId(expenseId);
//        expenseMemberRepository.deleteAll(expenseMembers);
//
//        // 새로운 ExpenseMember 저장
//        for (ExpenseRequestDTO.MemberShareDTO memberDTO : request.getMembers()) {
//            TripMember tripMember = tripMemberRepository.findByTripIdAndMemberId(tripId, memberDTO.getMemberId())
//                    .orElseThrow(() -> new IllegalArgumentException("Invalid Trip Member ID for tripId: " + tripId + " and memberId: " + memberDTO.getMemberId()));
//
//            ExpenseMember expenseMember = new ExpenseMember();
//            expenseMember.setShareAmount(memberDTO.getCost());
//            expenseMember.setTripMember(tripMember);
//            expenseMember.setExpense(expense);
//            expenseMemberRepository.save(expenseMember);
//        }
//    }

//    @Transactional
//    public void deleteExpense(Long tripId, Long expenseId) {
//        Expense expense = expenseRepository.findById(expenseId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 지출이 존재하지 않습니다: " + expenseId));
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다: " + tripId));
//
//        updateTotalCost(trip, -expense.getCost());
//        // Expense 삭제
//        expenseMemberRepository.deleteByExpenseId(expenseId); // 관련된 ExpenseMember 삭제
//        expenseRepository.delete(expense);
//    }

    @Transactional
    public void updateTotalCost(Trip trip, Integer costDifference) {
        if (trip.getTotalCost() == null) {
            trip.setTotalCost(0);
        }
        trip.setTotalCost(trip.getTotalCost() + costDifference);
        tripRepository.save(trip);
    }

    public ExpenseResponseDTO getExpensesByTrip(Long tripId) {
        // Trip 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다: " + tripId));

        // 남은 비용 계산
        int remainingCost = (trip.getBudget() != null ? trip.getBudget() : 0) -
                (trip.getTotalCost() != null ? trip.getTotalCost() : 0);

        // 카테고리별 비용 계산
        List<ExpenseResponseDTO.CategoryCostDTO> categories = expenseRepository.findCategoryCostsByTripId(tripId)
                .stream()
                .map(result -> {
                    ExpenseResponseDTO.CategoryCostDTO dto = new ExpenseResponseDTO.CategoryCostDTO();
                    dto.setCategory((String) result.get("category"));
                    dto.setCost(((Number) result.get("cost")).intValue());
                    return dto;
                })
                .collect(Collectors.toList());

        // 멤버 정보 조회
        List<ExpenseResponseDTO.MemberDTO> members = tripMemberRepository.findByTripId(tripId).stream()
                .map(tripMember -> {
                    ExpenseResponseDTO.MemberDTO memberDTO = new ExpenseResponseDTO.MemberDTO();
                    memberDTO.setMemberId(tripMember.getMember().getId());
                    memberDTO.setNickName(tripMember.getMember().getNickName());
                    return memberDTO;
                })
                .collect(Collectors.toList());

        ExpenseResponseDTO response = new ExpenseResponseDTO();
        response.setRemainingCost(remainingCost);
        response.setCategories(categories);
        response.setMembers(members);
        response.setBudget(trip.getBudget()); //budget 추가함


        return response;
    }

    public List<Map<String, Object>> getExpensesByDate(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다: " + tripId));
        List<Expense> expenses = expenseRepository.findByTripIdOrderByExpenseDateAsc(tripId);

        // 결과를 Map으로 변환
        return expenses.stream().map(expense -> {
            Map<String, Object> expenseMap = new HashMap<>();
            expenseMap.put("date", expense.getExpenseDate().toString());
            expenseMap.put("title", expense.getTitle());
            expenseMap.put("cost", expense.getAmount());
            return expenseMap;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getExpenseById(Long tripId, Long expenseId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다: " + tripId));
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지출이 존재하지 않습니다: " + expenseId));

        Map<String, Object> expenseDetails = new HashMap<>();
        expenseDetails.put("tripName", trip.getName());  // 여행 이름
        expenseDetails.put("expenseDate", expense.getExpenseDate().toString());
        expenseDetails.put("title", expense.getTitle());
        expenseDetails.put("amount", expense.getAmount());
        expenseDetails.put("expenseImage", expense.getExpenseImageUrl());  // 지출 이미지 URL
        expenseDetails.put("memo", expense.getMemo());

        return expenseDetails;
    }


}
