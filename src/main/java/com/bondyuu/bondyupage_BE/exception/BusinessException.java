package com.bondyuu.bondyupage_BE.exception;

import com.bondyuu.bondyupage_BE.error.ErrorCode;
import lombok.Getter;

/* 공통 비즈니스 에러를 위한 익셉션 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}