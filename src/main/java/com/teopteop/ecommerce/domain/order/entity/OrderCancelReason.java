package com.teopteop.ecommerce.domain.order.entity;

public enum OrderCancelReason {
    //고객 사유
    CHANGE_OF_MIND("단순 변심: 상품이 마음에 들지 않음"),
    WRONG_OPTION_SELECTED("잘못된 옵션 선택: 색상, 사이즈 등 옵션을 잘못 선택함"),
    DUPLICATE_ORDER("중복 주문: 동일 상품을 실수로 중복 주문함"),
    DELAYED_DELIVERY("배송 지연: 배송 완료 후 예상보다 늦은 배송으로 인한 취소"),

    // 관리자 사유
    OUT_OF_STOCK("재고 부족: 재고 부족으로 인한 판매자 취소"),
    ADMIN_FORCE("관리자 강제 취소: 운영 정책에 따른 관리자 직권 취소");

    private final String description;

    OrderCancelReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
