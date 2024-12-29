package com.Dodutch_Server.domain.trip.controller;

import com.Dodutch_Server.domain.auth.dto.response.KakaoResponseDto;
import com.Dodutch_Server.domain.auth.util.SecurityUtil;
import com.Dodutch_Server.domain.trip.dto.request.TripJoinRequestDto;
import com.Dodutch_Server.domain.trip.dto.request.TripUpdateRequestDTO;
import com.Dodutch_Server.domain.trip.dto.response.TripInfoResponseDto;
import com.Dodutch_Server.domain.trip.dto.response.TripResponse;
import com.Dodutch_Server.domain.trip.dto.response.TripResponseDTO;
import com.Dodutch_Server.domain.trip.dto.response.TripShareResponseDto;
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
    public ApiResponse<TripShareResponseDto> getShareTrip(@PathVariable("tripId") Long tripId) {

        return ApiResponse.onSuccess(tripService.convertToTripResponse(tripId));

    }

    @GetMapping("/{tripId}")
    @Operation(summary = "여행 정보 반환 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<TripInfoResponseDto>getTripInfo(@PathVariable("tripId") Long tripId) {
        TripInfoResponseDto tripInfoResponseDto = tripService.getTripResponseById(tripId);
        return ApiResponse.onSuccess(tripInfoResponseDto);
    }


    @PatchMapping("/{tripId}")
    @Operation(summary = "여행 수정 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> updateTrip(@PathVariable("tripId") Long tripId, @RequestBody TripUpdateRequestDTO request) {

        tripService.updateTrip(tripId, request);

        return ApiResponse.onSuccess();
    }


    @DeleteMapping("/{tripId}")
    @Operation(summary = "여행 삭제 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> deleteTrip(@PathVariable("tripId") Long tripId) {
        tripService.deleteTrip(tripId);
        return ApiResponse.onSuccess();
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

}
