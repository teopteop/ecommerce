package com.teopteop.ecommerce.global.exception;

import com.teopteop.ecommerce.global.common.response.ApiError;
import com.teopteop.ecommerce.global.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //사용자 정의 예외
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        BaseErrorCode errorCode = e.getErrorCode();

        log.warn("[{}] {}", errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(
                        ApiError.of(
                                errorCode.getCode(),
                                errorCode.getMessage()
                        )
                ));
    }

    //시스템 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        ApiError.of(
                                "COMMON_500",
                                "서버 오류가 발생했습니다."
                        )
                ));
    }
}
