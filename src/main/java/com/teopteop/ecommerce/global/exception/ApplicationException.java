package com.teopteop.ecommerce.global.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException{

    private final BaseErrorCode errorCode;

    public ApplicationException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}