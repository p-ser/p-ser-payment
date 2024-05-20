package com.pser.payment.config.kafka;

public interface KafkaTopics {
    String RESERVATION_PAYMENT_VALIDATION_REQUIRED = "reservation.payment-validation-required";
    String DEPOSIT_PAYMENT_VALIDATION_REQUIRED = "deposit.payment-validation-required";
    String AUCTION_PAYMENT_VALIDATION_REQUIRED = "auction.payment-validation-required";
    String RESERVATION_REFUND_REQUIRED = "reservation.refund-required";
    String DEPOSIT_REFUND_REQUIRED = "deposit.refund-required";
    String AUCTION_REFUND_REQUIRED = "auction.refund-required";
}
