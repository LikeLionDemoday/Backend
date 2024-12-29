package com.Dodutch_Server.domain.dutch.controller;

import com.Dodutch_Server.domain.auth.util.SecurityUtil;
import com.Dodutch_Server.domain.dutch.dto.DutchDTO;
import com.Dodutch_Server.domain.dutch.entity.Dutch;
import com.Dodutch_Server.domain.dutch.repository.DutchRepository;
import com.Dodutch_Server.domain.dutch.service.DutchService;
import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.domain.trip.repository.TripMemberRepository;
import com.Dodutch_Server.domain.trip.repository.TripRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class DutchController {
    private final DutchRepository dutchRepository;
    private final TripRepository tripRepository;
    private final DutchService dutchService;
    private final TripMemberRepository tripMemberRepository;

    private boolean isValidTrip(Long tripId) {
        return tripRepository.existsById(tripId);
    }

    @GetMapping("/dutch")
    public ResponseEntity<?> getAllDutchList() {
        Long memberId = SecurityUtil.getCurrentUserId();

        List<Dutch> dutchList = dutchRepository.findByPayerIdOrPayeeId(memberId, memberId);

        List<DutchResponseDTO> responseDTOs = dutchList.stream()
                .map(DutchResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(
                new ApiResponse<>(true, "200", "성공", Map.of("dutch", responseDTOs))
        );
    }

    @GetMapping("/trip/{tripId}/dutch")
    public ResponseEntity<?> getDutchList(@PathVariable Long tripId) {
        if (!isValidTrip(tripId)) {  // tripId 유효성 검사
            return ResponseEntity.status(404).body(
                    new ApiResponse<>(false, "404", "해당 여행이 존재하지 않습니다", null)
            );
        }

        Long memberId = SecurityUtil.getCurrentUserId();

        List<Dutch> dutchList = dutchRepository.findByTripId(tripId).stream()
                .filter(dutch -> dutch.getPayer().getId().equals(memberId) ||
                                 dutch.getPayee().getId().equals(memberId))
                .collect(Collectors.toList());


        List<DutchResponseDTO> responseDTOs = dutchList.stream()
                .map(DutchResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(
                new ApiResponse<>(true, "200", "성공", Map.of("dutch", responseDTOs))
        );
    }

    @GetMapping("/trip/{tripId}/dutch/{dutchId}")
    public ResponseEntity<?> getDutchDetail(@PathVariable Long tripId, @PathVariable Long dutchId) {
        if (!isValidTrip(tripId)) {  // tripId 유효성 검사
            return ResponseEntity.status(404).body(
                    new ApiResponse<>(false, "404", "해당 여행이 존재하지 않습니다", null)
            );
        }

        Optional<Dutch> dutch = dutchRepository.findByTripIdAndId(tripId, dutchId);

        if (dutch.isPresent()) {
            DutchResponseDTO responseDTO = DutchResponseDTO.fromEntity(dutch.get());
            return ResponseEntity.ok().body(
                    new ApiResponse<>(true, "200", "성공", responseDTO)
            );
        }
        return ResponseEntity.status(404).body(
                new ApiResponse<>(false, "404", "정산 내역이 존재하지 않습니다", null)
        );
    }

    @PatchMapping("/trip/{tripId}/dutch/{dutchId}")
    public ResponseEntity<?> markDutchAsCompleted(@PathVariable Long tripId, @PathVariable Long dutchId, @RequestBody DutchUpdateRequest request) {
        if (!isValidTrip(tripId)) {  // tripId 유효성 검사
            return ResponseEntity.status(404).body(
                    new ApiResponse<>(false, "404", "해당 여행이 존재하지 않습니다", null)
            );
        }

        Optional<Dutch> optionalDutch = dutchRepository.findByTripIdAndId(tripId, dutchId);
        if (optionalDutch.isPresent()) {
            Dutch dutch = optionalDutch.get();
            dutch.setIsCompleted(request.getIsCompleted());
            dutchRepository.save(dutch);

            DutchResponseDTO responseDTO = DutchResponseDTO.fromEntity(dutch);

            return ResponseEntity.ok().body(
                    new ApiResponse<>(true, "200", "성공", responseDTO)
            );
        }
        return ResponseEntity.status(404).body(
                new ApiResponse<>(false, "404", "정산 내역이 존재하지 않습니다", null)
        );
    }

    @PostMapping("/trip/{tripId}/dutch/calculate")
    public ResponseEntity<?> calculateDutch(@PathVariable("tripId") Long tripId) {
        if (!isValidTrip(tripId)) {
            return ResponseEntity.status(404).body(
                    new ApiResponse<>(false, "404", "해당 여행이 존재하지 않습니다", null)
            );
        }

        List<DutchDTO> calculatedDutchList = dutchService.calculateSettlement(tripId);

        dutchService.saveSettlementToDatabase(tripId, calculatedDutchList);

        return ResponseEntity.ok().body(
                new ApiResponse<>(true, "200", "정산 계산 성공", Map.of("calculatedDutch", calculatedDutchList))
        );
    }

    @GetMapping("/test/{tripId}")
    public ApiResponse<List<TripMember>> testTripMember(@PathVariable Long tripId){
        return new ApiResponse<>(true,"200","성공",tripMemberRepository.findByTripId(tripId));
    }


    @Getter
    @Setter
    public static class ApiResponse<T> {
        private boolean isSuccess;
        private String code;
        private String message;
        private T data;

        public ApiResponse(boolean isSuccess, String code, String message, T data) {
            this.isSuccess = isSuccess;
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }

    @Getter
    @Setter
    public static class DutchResponseDTO {
        private Long id;
        private Long payerId;
        private Long payeeId;
        private Integer perCost;
        private Boolean isCompleted;

        public static DutchResponseDTO fromEntity(Dutch dutch) {
            DutchResponseDTO dto = new DutchResponseDTO();
            dto.setId(dutch.getId());
            dto.setPayerId(dutch.getPayer() != null ? dutch.getPayer().getId() : null);
            dto.setPayeeId(dutch.getPayee() != null ? dutch.getPayee().getId() : null);
            dto.setPerCost(dutch.getPerCost());
            dto.setIsCompleted(dutch.getIsCompleted());
            return dto;
        }
    }

    @Getter
    @Setter
    public static class DutchUpdateRequest{
        private Boolean isCompleted;
    }
}