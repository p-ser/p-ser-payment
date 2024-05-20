package com.pser.payment.infra.kafka.consumer;

import com.pser.payment.application.PaymentService;
import com.pser.payment.config.kafka.KafkaTopics;
import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionPaymentValidationRequiredConsumer {
    private final PaymentService paymentService;

    @RetryableTopic(kafkaTemplate = "paymentDtoValueKafkaTemplate", attempts = "5")
    @KafkaListener(topics = KafkaTopics.AUCTION_PAYMENT_VALIDATION_REQUIRED, groupId = "${kafka.consumer-group-id}", containerFactory = "paymentDtoValueListenerContainerFactory")
    public void validateDepositPayment(PaymentDto paymentDto) {
        paymentService.validatePayment(ServiceEnum.AUCTION, paymentDto);
    }

    @DltHandler
    public void dltHandler(ConsumerRecord<String, PaymentDto> record) {
        PaymentDto paymentDto = record.value();
        paymentService.rollbackToPaymentRequired(ServiceEnum.AUCTION, paymentDto.getMerchantUid());
    }
}
