package com.teopteop.ecommerce.domain.order.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class OrderException extends BaseException {
    public OrderException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
