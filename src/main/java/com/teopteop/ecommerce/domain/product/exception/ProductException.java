package com.teopteop.ecommerce.domain.product.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class ProductException extends BaseException {
    public ProductException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
