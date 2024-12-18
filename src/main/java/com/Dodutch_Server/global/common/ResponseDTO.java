package com.Dodutch_Server.global.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO<T> {
    private boolean isSuccess;
    private String code;
    private String message;
    private T data;

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}