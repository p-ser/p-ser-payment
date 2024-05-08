package com.pser.payment.config.kafka;

public interface KafkaTopics {
    String RESERVATION_CONFIRM_AWAITING = "reservation.confirm-awaiting";
    String DEPOSIT_CONFIRM_AWAITING = "deposit.confirm-awaiting";
    String RESERVATION_REFUND_AWAITING = "reservation.refund-awaiting";
    String DEPOSIT_REFUND_AWAITING = "deposit.refund-awaiting";
}
