package com.teopteop.ecommerce.domain.customer.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class CustomerException extends BaseException {
    public CustomerException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
