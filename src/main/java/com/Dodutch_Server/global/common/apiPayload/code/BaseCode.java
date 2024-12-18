package com.Dodutch_Server.global.common.apiPayload.code;

import com.Dodutch_Server.global.common.apiPayload.code.dto.ReasonDTO;

public interface BaseCode {

    public ReasonDTO getReason();

    public ReasonDTO getReasonHttpStatus();
}