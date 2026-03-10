package com.teopteop.ecommerce.global.security.jwt;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class JwtException extends BaseException {
    public JwtException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
