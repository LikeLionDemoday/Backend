package com.Dodutch_Server.global.common.exception;

import com.Dodutch_Server.global.common.apiPayload.code.BaseErrorCode;
import com.Dodutch_Server.global.common.apiPayload.code.dto.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
