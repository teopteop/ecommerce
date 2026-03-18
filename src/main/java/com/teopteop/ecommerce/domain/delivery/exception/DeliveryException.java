package com.teopteop.ecommerce.domain.delivery.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class DeliveryException extends BaseException {
    public DeliveryException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

