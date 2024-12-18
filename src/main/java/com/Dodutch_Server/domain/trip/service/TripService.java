package com.Dodutch_Server.domain.trip.service;

import com.Dodutch_Server.domain.member.domain.repository.MemberRepository;
import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.trip.dto.TripAddMemberRequestDTO;
import com.Dodutch_Server.domain.trip.dto.TripRequestDTO;
import com.Dodutch_Server.domain.trip.dto.TripResponseDTO;
import com.Dodutch_Server.domain.trip.entity.Trip;
import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.domain.trip.repository.TripMemberRepository;
import com.Dodutch_Server.domain.trip.repository.TripRepository;
import com.Dodutch_Server.global.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;


    public TripResponseDTO convertToTripResponse(Trip trip) {
        TripResponseDTO tripResponse = new TripResponseDTO();
        tripResponse.setTripId(trip.getId());
        tripResponse.setTripName(trip.getName());
        tripResponse.setStartDate(trip.getStartDate().toString());
        tripResponse.setEndDate(trip.getEndDate().toString());
        tripResponse.setPlace(trip.getPlace());
        tripResponse.setBudget(trip.getBudget().longValue());
        tripResponse.setQrCode(trip.getQrCode());
        return tripResponse;
    }

    // 여행 조회
    public Trip getTripById(Long tripId) {
        return tripRepository.findById(tripId).orElseThrow(() ->
                new IllegalArgumentException("해당 여행이 존재하지 않습니다"));
    }

    // 여행 삭제
    public void deleteTrip(Long tripId) {
        Trip trip = getTripById(tripId);
        tripRepository.delete(trip);
    }

    // 여행 수정
    public Trip updateTrip(Trip trip, TripRequestDTO tripRequestDTO) {
        if (tripRequestDTO.getTripName() != null) trip.setName(tripRequestDTO.getTripName());
        if (tripRequestDTO.getStartDate() != null) trip.setStartDate(tripRequestDTO.getStartDate());
        if (tripRequestDTO.getEndDate() != null) trip.setEndDate(tripRequestDTO.getEndDate());
        if (tripRequestDTO.getPlace() != null) trip.setPlace(tripRequestDTO.getPlace());
        if (tripRequestDTO.getBudget() != null) trip.setBudget(tripRequestDTO.getBudget());
        return tripRepository.save(trip);
    }

    // 여행명과 날짜로 검색
    public List<TripResponseDTO> searchNameAndDate(String name, String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        List<Trip> trips = tripRepository.findByNameContainingAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                name, parsedDate, parsedDate
        );
        return trips.stream().map(this::convertToTripResponse).collect(Collectors.toList());
    }

    // 날짜로 검색
    public List<TripResponseDTO> searchDate(String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        List<Trip> trips = tripRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(parsedDate, parsedDate);
        return trips.stream().map(this::convertToTripResponse).collect(Collectors.toList());
    }

    // 여행명으로 부분 일치 검색
    public List<TripResponseDTO> searchName(String name) {
        List<Trip> trips = tripRepository.findByNameContaining(name);
        return trips.stream().map(this::convertToTripResponse).collect(Collectors.toList());
    }

    // 모든 여행 가져오기
    public List<TripResponseDTO> getAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        return trips.stream().map(this::convertToTripResponse).collect(Collectors.toList());
    }

    // 멤버를 여행에 추가하는 메소드
    public void addMemberToTrip(TripAddMemberRequestDTO request) {
        // 여행 조회
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다."));

        // QR 코드 검증
        if (!trip.getQrCode().equals(request.getQrCode())) {
            throw new IllegalArgumentException("유효하지 않은 QR 코드입니다.");
        }

        // 멤버 조회
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        // TripMember 저장
        TripMember tripMember = new TripMember();
        tripMember.setTrip(trip);
        tripMember.setMember(member);
        tripMemberRepository.save(tripMember);
    }


}
