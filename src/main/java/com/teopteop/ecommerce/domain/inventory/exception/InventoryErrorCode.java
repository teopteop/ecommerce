package com.teopteop.ecommerce.domain.inventory.exception;

import com.teopteop.ecommerce.global.exception.BaseErrorCode;
import org.springframework.http.HttpStatus;

public enum InventoryErrorCode implements BaseErrorCode {
    STOCK_QUANTITY_POSITIVE(HttpStatus.BAD_REQUEST, "재고 수량은 0 이상이어야 합니다."),
    INVALID_INVENTORY_QUANTITY(HttpStatus.BAD_REQUEST, "재고 변경 수량은 0보다 커야 합니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_PRODUCT_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 상품 ID입니다."),
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 인벤토리를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    InventoryErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
