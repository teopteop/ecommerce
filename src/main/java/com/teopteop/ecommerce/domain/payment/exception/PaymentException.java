package com.teopteop.ecommerce.domain.payment.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class PaymentException extends BaseException {
    public PaymentException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
