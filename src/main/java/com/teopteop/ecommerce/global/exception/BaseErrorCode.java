package com.teopteop.ecommerce.global.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
