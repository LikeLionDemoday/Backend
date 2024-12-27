package com.Dodutch_Server.domain.trip.controller;

import com.Dodutch_Server.domain.auth.dto.response.KakaoResponseDto;
import com.Dodutch_Server.domain.auth.util.SecurityUtil;
import com.Dodutch_Server.domain.trip.dto.request.TripJoinRequestDto;
import com.Dodutch_Server.domain.trip.dto.response.TripResponseDTO;
import com.Dodutch_Server.domain.trip.util.RandomStringGenerator;
import com.Dodutch_Server.domain.trip.repository.TripRepository;
import com.Dodutch_Server.global.common.ResponseDTO;
import com.Dodutch_Server.domain.trip.dto.request.TripRequestDTO;
import com.Dodutch_Server.domain.trip.entity.Trip;
import com.Dodutch_Server.domain.trip.service.TripService; // TripService 추가
import com.Dodutch_Server.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
@Tag(name = "Trip", description = "여행 관련된 API")
public class TripController {

    private final TripService tripService;
    private final TripRepository tripRepository;


    @PostMapping
    @Operation(summary = "여행 생성 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<TripResponseDTO> create(@ModelAttribute TripRequestDTO request) {

            Long memberId = SecurityUtil.getCurrentUserId();
            TripResponseDTO tripResponseDTO = tripService.createTrip(request,memberId);
        return ApiResponse.onSuccess(tripResponseDTO);
    }

    @PostMapping("/join")
    @Operation(summary = "여행 참여 API")
    public ApiResponse<Object> joinTripWithCode(@RequestBody TripJoinRequestDto request) {
        Long memberId = SecurityUtil.getCurrentUserId();

        tripService.addMemberToTrip(request.getJoinCode(), memberId);

        return ApiResponse.onSuccess();
        }


    @GetMapping("/{tripId}/code")
    @Operation(summary = "참여 코드 반환 API")
    public ApiResponse<String> getTripJoinCode(@PathVariable Long tripId) {

        return ApiResponse.onSuccess(tripService.getJoinCode(tripId));

    }


    @GetMapping("/{tripId}")
    public ResponseDTO<TripResponseDTO> getTrip(@PathVariable Long tripId) {
        Trip trip = tripService.getTripById(tripId);
        TripResponseDTO tripResponse = tripService.convertToTripResponse(trip);
        return createSuccessResponse("200", "여행 조회 성공", tripResponse);
    }

    @PatchMapping("/{tripId}")
    public ResponseDTO<TripResponseDTO> updateTrip(@PathVariable Long tripId, @RequestBody TripRequestDTO tripRequestDTO) {
        Trip trip = tripService.getTripById(tripId);
        Trip updatedTrip = tripService.updateTrip(trip, tripRequestDTO); // 변수명으로 수정

        TripResponseDTO tripResponse = tripService.convertToTripResponse(updatedTrip);
        return createSuccessResponse("200", "여행 수정 성공", tripResponse);
    }


    @DeleteMapping("/{tripId}")
    public ResponseDTO<Object> deleteTrip(@PathVariable Long tripId) {
        try {
            tripService.deleteTrip(tripId);
            return createSuccessResponse("200", "삭제 성공", null); // data를 null로 반환
        } catch (IllegalArgumentException e) {
            return createErrorResponse("404", "존재하지 않는 여행입니다");
        }
    }

    @GetMapping("/search")
    public ResponseDTO<List<TripResponseDTO>> searchTrips(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String date,
                                                          @RequestParam(required = false) Long member) {
        List<TripResponseDTO> tripResponseList;

        if (name == null && date == null && member == null) {
            tripResponseList = tripService.getAllTrips();
        } else {
            tripResponseList = tripService.searchTrips(name, date, member);
        }

        if (tripResponseList.isEmpty()) {
            return createErrorResponse("200", "해당하는 여행이 없습니다");
        }

        return createSuccessResponse("200", "여행 검색 성공", tripResponseList);
    }

    // 공통 응답 생성 메소드 (성공)
    private <T> ResponseDTO<T> createSuccessResponse(String code, String message, T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setIsSuccess(true);
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    // 공통 응답 생성 메소드 (실패)
    private <T> ResponseDTO<T> createErrorResponse(String code, String message) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setIsSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }


}
