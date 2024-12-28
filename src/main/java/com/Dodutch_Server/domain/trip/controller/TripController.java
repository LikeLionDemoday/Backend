package com.Dodutch_Server.domain.trip.controller;

import com.Dodutch_Server.domain.auth.dto.response.KakaoResponseDto;
import com.Dodutch_Server.domain.auth.util.SecurityUtil;
import com.Dodutch_Server.domain.trip.dto.request.TripJoinRequestDto;
import com.Dodutch_Server.domain.trip.dto.request.TripUpdateRequestDTO;
import com.Dodutch_Server.domain.trip.dto.response.TripResponse;
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
    public ApiResponse<Long> create(@ModelAttribute TripRequestDTO request) {

            Long memberId = SecurityUtil.getCurrentUserId();
            Long tripId = tripService.createTrip(request,memberId);
        return ApiResponse.onSuccess(tripId);
    }

    @PostMapping("/join")
    @Operation(summary = "여행 참여 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> joinTripWithCode(@RequestBody TripJoinRequestDto request) {
        Long memberId = SecurityUtil.getCurrentUserId();

        tripService.addMemberToTrip(request.getJoinCode(), memberId);

        return ApiResponse.onSuccess();
        }

    @GetMapping("/share/{tripId}")
    @Operation(summary = "여행 공유시 정보 반환 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<TripResponseDTO> getTripInfo(@PathVariable Long tripId) {

        return ApiResponse.onSuccess(tripService.convertToTripResponse(tripId));

    }

    @GetMapping("/{tripId}")
    @Operation(summary = "여행 정보 반환 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<TripResponse>getTrip(@PathVariable Long tripId) {
        TripResponse tripResponse = tripService.getTripResponseById(tripId);
        return ApiResponse.onSuccess(tripResponse);
    }


    @PatchMapping("/{tripId}")
    @Operation(summary = "여행 수정 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> updateTrip(@PathVariable Long tripId, @RequestBody TripUpdateRequestDTO request) {

        tripService.updateTrip(tripId, request);

        return ApiResponse.onSuccess();
    }


    @DeleteMapping("/{tripId}")
    @Operation(summary = "여행 삭제 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ResponseDTO<Object> deleteTrip(@PathVariable Long tripId) {
        try {
            tripService.deleteTrip(tripId);
            return createSuccessResponse("200", "삭제 성공", null); // data를 null로 반환
        } catch (IllegalArgumentException e) {
            return createErrorResponse("404", "존재하지 않는 여행입니다");
        }
    }

    @GetMapping("/search")
    @Operation(summary = "여행 검색 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<List<TripResponse>> getMyTrips() {

        List<TripResponse> trips = tripService.getAllTrips();

        return ApiResponse.onSuccess(trips);
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
