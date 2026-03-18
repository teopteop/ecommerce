package com.teopteop.ecommerce.domain.delivery.entity;

public enum DeliveryStatus {
    PENDING(0),
    SHIPPED(1),
    DELIVERED(2);

    private final int priority;

    DeliveryStatus(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
