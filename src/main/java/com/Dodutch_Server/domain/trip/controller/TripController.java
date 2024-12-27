package com.Dodutch_Server.domain.trip.controller;

import com.Dodutch_Server.domain.trip.dto.TripResponseDTO;
import com.Dodutch_Server.domain.trip.util.RandomStringGenerator;
import com.Dodutch_Server.domain.trip.repository.TripRepository;
import com.Dodutch_Server.global.common.ResponseDTO;
import com.Dodutch_Server.domain.trip.dto.TripRequestDTO;
import com.Dodutch_Server.domain.trip.entity.Trip;
import com.Dodutch_Server.domain.trip.service.TripService; // TripService 추가
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService; // TripService 사용
    private final TripRepository tripRepository; // TripRepository 추가


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<TripResponseDTO> createTrip(@RequestBody TripRequestDTO tripRequestDTO) {
        if (tripRequestDTO.getTripName() == null || tripRequestDTO.getStartDate() == null ||
                tripRequestDTO.getEndDate() == null || tripRequestDTO.getPlace() == null || tripRequestDTO.getBudget() == null) {

            return createErrorResponse("400", "필수 항목이 누락되었습니다.");
        }

        String joinCode = RandomStringGenerator.generateRandomString(12);

        // 여행 엔티티 생성 및 저장
        Trip trip = new Trip();
        trip.setName(tripRequestDTO.getTripName());
        trip.setStartDate(tripRequestDTO.getStartDate());
        trip.setEndDate(tripRequestDTO.getEndDate());
        trip.setPlace(tripRequestDTO.getPlace());
        trip.setBudget(tripRequestDTO.getBudget());
        trip.setTotalCost(0); // 초기 비용 설정
        trip.setJoinCode(joinCode); // 참여 코드 설정
        trip.setMemo(tripRequestDTO.getMemo()); //메모 추가

        Trip savedTrip = tripRepository.save(trip); // TripRepository 인스턴스를 통해 save 호출

        TripResponseDTO tripResponse = tripService.convertToTripResponse(savedTrip);

        return createSuccessResponse("201", "여행 생성 성공", tripResponse);
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


    @PostMapping("/join")
    public ResponseEntity<ResponseDTO<Void>> joinTripWithCode(@RequestBody Map<String, Object> request) {
        String joinCode = (String) request.get("joinCode");
        Long memberId = ((Number) request.get("memberId")).longValue();

        if (joinCode == null || memberId == null) {
            return ResponseEntity.badRequest().body(
                    createErrorResponse("400", "Required parameters 'joinCode' and 'memberId' are not present.")
            );
        }

        try {
            // 참여 코드로 Trip 찾기
            Trip trip = tripRepository.findByJoinCode(joinCode)
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 참여 코드입니다."));
            tripService.addMemberToTrip(trip.getId(), memberId);

            return ResponseEntity.ok(
                    createSuccessResponse("200", "멤버가 성공적으로 추가되었습니다.", null)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    createErrorResponse("400", e.getMessage())
            );
        }
    }


    @GetMapping("/{tripId}/join") //참여 코드 반환
    public ResponseEntity<ResponseDTO<Map<String, String>>> getTripJoinCode(@PathVariable Long tripId) {
        // Trip ID로 여행 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다."));

        Map<String, String> response = Map.of("joinCode", trip.getJoinCode());
        return ResponseEntity.ok(
                createSuccessResponse("200", "참여 코드 반환 성공", response)
        );
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
