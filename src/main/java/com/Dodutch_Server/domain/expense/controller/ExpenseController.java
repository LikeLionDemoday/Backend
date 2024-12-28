package com.Dodutch_Server.domain.expense.controller;

import com.Dodutch_Server.domain.auth.util.SecurityUtil;
import com.Dodutch_Server.domain.expense.dto.ExpenseRequestDTO;
import com.Dodutch_Server.domain.expense.dto.ExpenseResponseDTO;
import com.Dodutch_Server.global.common.ResponseDTO;
import com.Dodutch_Server.domain.expense.service.ExpenseService;
import com.Dodutch_Server.global.common.apiPayload.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip")
@Tag(name = "Expense", description = "지출 관련된 API")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping(value = "/{tripId}/expense", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "지출 생성 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> addExpense(@PathVariable("tripId") Long tripId,
                                          @RequestPart("request") @Valid String requestJson,
                                          @RequestPart(value = "expenseImage", required = false) MultipartFile expenseImage) {


        // JSON 문자열을 DTO로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        ExpenseRequestDTO request;
        try {
            request = objectMapper.readValue(requestJson, ExpenseRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format in 'request' field", e);
        }


        expenseService.addExpense(tripId,request.getPayer(), request, expenseImage);

        return ApiResponse.onSuccess();
    }

    @GetMapping("/{tripId}/expense/date")
    public ResponseDTO<List<Map<String, Object>>> getExpensesByDate(@PathVariable Long tripId) {
        try {
            // Service 호출
            List<Map<String, Object>> expenseByDate = expenseService.getExpensesByDate(tripId);

            // 성공 응답 반환
            return createSuccessResponse("200", "성공이요", expenseByDate);
        } catch (IllegalArgumentException e) {
            return createErrorResponse("400", e.getMessage());
        }
    }


    @GetMapping("/{tripId}/expense")
    public ResponseDTO<ExpenseResponseDTO> getExpensesByTrip(@PathVariable Long tripId) {
        try {
            ExpenseResponseDTO responseData = expenseService.getExpensesByTrip(tripId);
            return createSuccessResponse("200", "성공이요", responseData);
        } catch (IllegalArgumentException e) {
            return createErrorResponse("400", e.getMessage());
        }
    }

//    @PatchMapping("/{tripId}/expense/{expenseId}")
//    public ResponseDTO<Void> updateExpense(@PathVariable Long tripId, @PathVariable Long expenseId, @RequestBody ExpenseRequestDTO request) {
//        try {
//            expenseService.updateExpense(tripId, expenseId, request);
//            return createSuccessResponse("200", "지출이 수정되었습니다.", null);
//        } catch (IllegalArgumentException e) {
//            return createErrorResponse("400", e.getMessage());
//        }
//    }

//    @DeleteMapping("/{tripId}/expense/{expenseId}")
//    public ResponseDTO<Void> deleteExpense(@PathVariable Long tripId, @PathVariable Long expenseId) {
//        try {
//            expenseService.deleteExpense(tripId, expenseId);
//            return createSuccessResponse("200", "지출이 삭제되었습니다.", null);
//        } catch (IllegalArgumentException e) {
//            return createErrorResponse("400", e.getMessage());
//        }
//    }


    private <T> ResponseDTO<T> createSuccessResponse(String code, String message, T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setIsSuccess(true);
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    private <T> ResponseDTO<T> createErrorResponse(String code, String message) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setIsSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
}
