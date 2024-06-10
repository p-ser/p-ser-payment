package com.pser.payment.infra.kafka.consumer;

import com.pser.payment.application.PaymentService;
import com.pser.payment.config.kafka.KafkaTopics;
import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.RefundDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositRefundRequiredConsumer {
    private final PaymentService paymentService;

    @RetryableTopic(kafkaTemplate = "refundDtoValueKafkaTemplate", attempts = "5", retryTopicSuffix = "-retry-${kafka.consumer-group-id}")
    @KafkaListener(topics = KafkaTopics.DEPOSIT_REFUND_REQUIRED, groupId = "${kafka.consumer-group-id}", containerFactory = "refundDtoValueListenerContainerFactory")
    public void refundDeposit(RefundDto refundDto) {
        paymentService.refund(ServiceEnum.DEPOSIT, refundDto);
    }
}
