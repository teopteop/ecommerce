package com.teopteop.ecommerce.global.exception;

import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * ApplicationException 처리
     * - 사용자 정의 예외 처리
     * - BaseErrorCode에 정의된 상태코드와 메세지를 기반으로 Response 반환
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(ApplicationException e) {
        BaseErrorCode errorCode = e.getErrorCode();

        log.warn("요청 처리 실패: status={}, message={}", errorCode.getStatus(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(
                        errorCode.getMessage()
                ));
    }

    /**
     * 시스템 예외 처리
     * - 개발자가 처리하지 않은 모든 예외 처리
     * - Internal Server Error (500) 반환
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버 오류가 발생했습니다."));
    }
}
