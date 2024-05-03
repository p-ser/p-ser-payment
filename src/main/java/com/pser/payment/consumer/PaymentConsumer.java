package com.pser.payment.consumer;

import com.pser.payment.application.PaymentService;
import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.ConfirmDto;
import com.pser.payment.dto.RefundDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = "deposit-confirm-awaiting", groupId = "${kafka.consumer-group-id}", containerFactory = "confirmDtoValueListenerContainerFactory")
    public void confirmDeposit(ConfirmDto confirmDto) {
        paymentService.confirm(ServiceEnum.DEPOSIT, confirmDto);
    }

    @KafkaListener(topics = "reservation-confirm-awaiting", groupId = "${kafka.consumer-group-id}", containerFactory = "confirmDtoValueListenerContainerFactory")
    public void confirmReservation(ConfirmDto confirmDto) {
        paymentService.confirm(ServiceEnum.RESERVATION, confirmDto);
    }

    @KafkaListener(topics = "deposit-refund-awaiting", groupId = "${kafka.consumer-group-id}", containerFactory = "refundDtoValueListenerContainerFactory")
    public void refundDeposit(RefundDto refundDto) {
        paymentService.refund(ServiceEnum.DEPOSIT, refundDto);
    }

    @KafkaListener(topics = "reservation-refund-awaiting", groupId = "${kafka.consumer-group-id}", containerFactory = "refundDtoValueListenerContainerFactory")
    public void refundReservation(RefundDto refundDto) {
        paymentService.refund(ServiceEnum.RESERVATION, refundDto);
    }

}
