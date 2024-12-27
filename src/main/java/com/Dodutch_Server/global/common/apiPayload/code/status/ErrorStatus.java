package com.Dodutch_Server.global.common.apiPayload.code.status;

import com.Dodutch_Server.global.common.apiPayload.code.BaseErrorCode;
import com.Dodutch_Server.global.common.apiPayload.code.dto.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    // 멤버 관련 응답
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "멤버가 없습니다."),
    MEMBER_NICKNAME_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "중복된 닉네임입니다"),

    // 여행 관련 응답

    // 정산 관련 응답

    // 기타
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"CATEGORY4001", "카테고리가 없습니다."),

    // 인증 관련
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"AUTH401","인증되지 않은 요청입니다"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH401","유효하지않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH401","유효하지않은 리프레시 토큰입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"AUTH400","잘못된 요청입니다."),
    NOT_EXIST_USER(HttpStatus.UNAUTHORIZED,"AUTH401","존재하지 않는 유저입니다.")

    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
