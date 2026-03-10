package com.teopteop.ecommerce.global.exception;

import com.teopteop.ecommerce.global.common.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.BindException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * BaseException 처리
     * - 사용자 정의 예외 처리
     * - BaseErrorCode에 정의된 상태코드와 메세지를 기반으로 Response 반환
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        BaseErrorCode errorCode = e.getErrorCode();

        log.warn("요청 처리 실패: status={}, message={}", errorCode.getStatus(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(
                        errorCode.getMessage()
                ));
    }

    /**
     * DTO 바인딩 검증 예외 처리
     * - @Valid / @RequestBody 바인딩 과정에서 발생하는 검증 실패 처리
     * - json 요청 바디의 필드 유효성 검증 실패 시 발생
     * - HTTP 400 (Bad Request) 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", errors);

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errors));
    }

    /**
     * 단일 파라미터 제약조건 위반 예외 처리
     * - @Validated 적용 컨트롤러에서 @RequestParam, @PathVariable 제약 조건 위반 시 발생
     * - HTTP 400 (Bad Request) 반환
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        String errors = e.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String field = violation.getPropertyPath().toString();
                    return field + ": " + violation.getMessage();
                })
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", errors);

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errors));
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
