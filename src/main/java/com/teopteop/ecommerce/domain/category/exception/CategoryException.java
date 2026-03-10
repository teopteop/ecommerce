package com.teopteop.ecommerce.domain.category.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class CategoryException extends BaseException {
    public CategoryException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
