package com.teopteop.ecommerce.domain.auth.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class AuthException extends BaseException {
    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
