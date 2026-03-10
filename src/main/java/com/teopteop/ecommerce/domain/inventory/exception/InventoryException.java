package com.teopteop.ecommerce.domain.inventory.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class InventoryException extends BaseException {
    public InventoryException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
