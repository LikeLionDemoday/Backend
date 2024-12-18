package com.Dodutch_Server.global.common.apiPayload.code;

import com.Dodutch_Server.global.common.apiPayload.code.dto.ErrorReasonDTO;

public interface BaseErrorCode {

    public ErrorReasonDTO getReason();

    public ErrorReasonDTO getReasonHttpStatus();
}