package com.teopteop.ecommerce.domain.account.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class AccountException extends BaseException {
    public AccountException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
