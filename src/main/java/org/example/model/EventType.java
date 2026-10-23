package org.example.model;

/** Допустимые значения eventType для варианта «Kafka-like события». */
public enum EventType {
    ORDER_CREATED,
    ORDER_PAID,
    ORDER_CANCELLED,
    PAYMENT_STARTED,
    PAYMENT_FAILED,
    USER_REGISTERED
}
