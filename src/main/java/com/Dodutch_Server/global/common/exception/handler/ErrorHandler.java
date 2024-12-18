package com.Dodutch_Server.global.common.exception.handler;

import com.Dodutch_Server.global.common.apiPayload.code.BaseErrorCode;
import com.Dodutch_Server.global.common.exception.GeneralException;

public class ErrorHandler extends GeneralException {

    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);}
}
