package com.Dodutch_Server.domain.trip.service;

import com.Dodutch_Server.domain.member.repository.MemberRepository;
import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.trip.dto.request.TripRequestDTO;
import com.Dodutch_Server.domain.trip.dto.response.TripResponseDTO;
import com.Dodutch_Server.domain.trip.entity.Trip;
import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.domain.trip.repository.TripMemberRepository;
import com.Dodutch_Server.domain.trip.repository.TripRepository;
import com.Dodutch_Server.domain.trip.util.RandomStringGenerator;
import com.Dodutch_Server.domain.uuid.entity.Uuid;
import com.Dodutch_Server.domain.uuid.repository.UuidRepository;
import com.Dodutch_Server.global.common.apiPayload.code.status.ErrorStatus;
import com.Dodutch_Server.global.common.exception.handler.ErrorHandler;
import com.Dodutch_Server.global.config.aws.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    public TripResponseDTO createTrip(TripRequestDTO request, Long memberId){

        Uuid mainUuid = uuidRepository.save(Uuid.builder().uuid(UUID.randomUUID().toString()).build());
        String tripImageUrl = s3Manager.uploadFile(s3Manager.generateMainKeyName(mainUuid), request.getTripImage());

        String joinCode = RandomStringGenerator.generateRandomString(12);

        Trip trip = Trip.builder()
                .tripImageUrl(tripImageUrl)
                .name(request.getTripName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .place(request.getPlace())
                .budget(request.getBudget())
                .totalCost(0)
                .joinCode(joinCode)
                .build();

        Trip savedTrip = tripRepository.save(trip); // TripRepository 인스턴스를 통해 save 호출

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.NOT_EXIST_USER));

        TripMember tripMember = TripMember.builder()
                .member(findMember)
                .trip(savedTrip)
                .build();

        tripMemberRepository.save(tripMember);


        return convertToTripResponse(savedTrip);
    }


    public TripResponseDTO convertToTripResponse(Trip trip) {
        return TripResponseDTO.builder()
                .tripId(trip.getId())
                .joinCode(trip.getJoinCode())
                .tripImageUrl(trip.getTripImageUrl())
                .build();

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

    public void addMemberToTrip(Long tripId, Long memberId) {
        // 여행 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다."));

        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        // TripMember 중복 체크
        boolean isMemberAlreadyInTrip = trip.getTripMembers().stream()
                .anyMatch(tripMember -> tripMember.getMember().getId().equals(memberId));

        if (isMemberAlreadyInTrip) {
            throw new IllegalArgumentException("멤버가 이미 여행에 추가되어 있습니다.");
        }

        // TripMember 저장
        TripMember tripMember = new TripMember();
        tripMember.setTrip(trip);
        tripMember.setMember(member);
        tripMemberRepository.save(tripMember);
    }


    public List<TripResponseDTO> searchTrips(String name, String date, Long memberId) {
        Integer parsedYear = null;

        // 연도만 추출
        if (date != null) {
            try {
                parsedYear = Integer.parseInt(date);  // 연도만 받기
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("잘못된 연도 형식입니다: " + date);
            }
        }

        // memberId로 tripId 리스트 조회
        List<Long> tripIds = (memberId != null) ? tripMemberRepository.findTripIdsByMemberId(memberId) : null;

        // TripRepository에서 검색 수행
        List<Trip> trips = tripRepository.searchTrips(
                (name != null ? name.trim().replaceAll("\"", "") : null),
                parsedYear,
                tripIds
        );

        // 검색 결과를 DTO로 변환
        return trips.stream().map(this::convertToTripResponse).collect(Collectors.toList());
    }


    public List<TripResponseDTO> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::convertToTripResponse)
                .collect(Collectors.toList());
    }



}
