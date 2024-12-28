package com.Dodutch_Server.domain.trip.service;

import com.Dodutch_Server.domain.auth.util.SecurityUtil;
import com.Dodutch_Server.domain.member.repository.MemberRepository;
import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.trip.dto.request.TripRequestDTO;
import com.Dodutch_Server.domain.trip.dto.request.TripUpdateRequestDTO;
import com.Dodutch_Server.domain.trip.dto.response.TripResponse;
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


import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    // 여행 생성
    @Transactional
    public Long createTrip(TripRequestDTO request, Long memberId){

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

        return savedTrip.getId();
    }

    // 여행 참여
    @Transactional
    public void addMemberToTrip(String joinCode, Long memberId) {

        // 참여 코드로 Trip 찾기
        Trip findTrip = tripRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));


        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // TripMember 중복 체크
        boolean isMemberAlreadyInTrip = findTrip.getTripMembers().stream()
                .anyMatch(tripMember -> tripMember.getMember().getId().equals(memberId));

        if (isMemberAlreadyInTrip) {
            throw new ErrorHandler(ErrorStatus.TRIP_MEMBER_EXIST);
        }

        TripMember tripMember = TripMember.builder()
                .trip(findTrip)
                .member(findMember)
                .build();

        tripMemberRepository.save(tripMember);

    }


    public TripResponseDTO convertToTripResponse(Long tripId) {
        Trip findTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));
        return TripResponseDTO.builder()
                .tripImageUrl(findTrip.getTripImageUrl())
                .startDate(findTrip.getStartDate())
                .endDate(findTrip.getEndDate())
                .name(findTrip.getName())
                .place(findTrip.getPlace())
                .joinCode(findTrip.getJoinCode())
                .build();
    }

    public TripResponse convertToTripResponseV2(Trip trip) {
        TripResponse tripResponse = new TripResponse();
        tripResponse.setTripId(trip.getId());
        tripResponse.setTripName(trip.getName());
        tripResponse.setStartDate(trip.getStartDate().toString());
        tripResponse.setEndDate(trip.getEndDate().toString());
        tripResponse.setPlace(trip.getPlace());
        tripResponse.setBudget(trip.getBudget().longValue());
        tripResponse.setTotalCost(trip.getTotalCost()); // totalCost 추가

        // TripMember에서 해당 Trip에 속한 Member ID 리스트 생성
        List<TripResponse.MemberDTO> memberDTOList = trip.getTripMembers().stream()
                .map(tripMember -> {
                    TripResponse.MemberDTO memberDTO = new TripResponse.MemberDTO();
                    memberDTO.setMemberID(tripMember.getMember().getId());
                    return memberDTO;
                })
                .collect(Collectors.toList());

        tripResponse.setMembers(memberDTOList);

        return tripResponse;
    }

    public TripResponse getTripResponseById(Long tripId) {
        Trip findTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.TRIP_NOT_EXIST));
        return convertToTripResponseV2(findTrip); // Trip -> TripResponse 변환
    }


    // 참여 코드 반환
    @Transactional
    public String getJoinCode(Long tripId){
        return tripRepository.findById(tripId).get().getJoinCode();
    }

    // 여행 조회
    @Transactional
    public Trip getTripById(Long tripId) {
        return tripRepository.findById(tripId).orElseThrow(() ->
                new IllegalArgumentException("해당 여행이 존재하지 않습니다"));
    }

    // 여행 삭제
    @Transactional
    public void deleteTrip(Long tripId) {
        Trip trip = getTripById(tripId);
        tripRepository.delete(trip);
    }

    // 여행 수정
   @Transactional
    public void updateTrip(Long tripId, TripUpdateRequestDTO request) {
        Trip updateTrip  = getTripById(tripId);
        updateTrip.updateTripInfo(request);
        tripRepository.save(updateTrip);
    }


//    @Transactional
//    public List<TripResponse> searchTrips(String name, String date, Long memberId) {
//        Integer parsedYear = null;
//
//        // 연도만 추출
//        if (date != null) {
//            try {
//                parsedYear = Integer.parseInt(date);  // 연도만 받기
//            } catch (NumberFormatException e) {
//                throw new IllegalArgumentException("잘못된 연도 형식입니다: " + date);
//            }
//        }
//
//        // memberId로 tripId 리스트 조회
//        List<Long> tripIds = (memberId != null) ? tripMemberRepository.findTripIdsByMemberId(memberId) : null;
//
//        // TripRepository에서 검색 수행
//        List<Trip> trips = tripRepository.searchTrips(
//                (name != null ? name.trim().replaceAll("\"", "") : null),
//                parsedYear,
//                tripIds
//        );
//
//        // 검색 결과를 DTO로 변환
//        return trips.stream().map(this::convertToTripResponseV2).collect(Collectors.toList());
//    }


    @Transactional
    public List<TripResponse> getAllTrips() {
        Long memberId = SecurityUtil.getCurrentUserId();

        List<Long> tripIds = tripMemberRepository.findTripIdsByMemberId(memberId);

        if (tripIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Trip> trips = tripRepository.findAllById(tripIds);

        trips.sort((t1, t2) -> t2.getStartDate().compareTo(t1.getStartDate()));

        return trips.stream()
                .map(this::convertToTripResponseV2)
                .collect(Collectors.toList());
    }



}
