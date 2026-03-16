package com.teopteop.ecommerce.domain.seller.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class SellerException extends BaseException {
    public SellerException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
