package com.teopteop.ecommerce.domain.member.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import com.teopteop.ecommerce.global.exception.BaseException;

public class MemberException extends BaseException {
    public MemberException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
