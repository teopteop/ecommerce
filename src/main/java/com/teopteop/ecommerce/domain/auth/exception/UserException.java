package com.teopteop.ecommerce.domain.auth.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class UserException extends BaseException {
    public UserException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
