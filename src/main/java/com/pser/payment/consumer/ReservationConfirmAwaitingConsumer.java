package com.pser.payment.consumer;

import com.pser.payment.application.PaymentService;
import com.pser.payment.config.kafka.KafkaTopics;
import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.ConfirmDto;
import com.pser.payment.dto.RefundDto;
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
public class ReservationConfirmAwaitingConsumer {
    private final PaymentService paymentService;

    @RetryableTopic(kafkaTemplate = "confirmDtoValueKafkaTemplate", attempts = "5")
    @KafkaListener(topics = KafkaTopics.RESERVATION_CONFIRM_AWAITING, groupId = "${kafka.consumer-group-id}", containerFactory = "confirmDtoValueListenerContainerFactory")
    public void confirmReservation(ConfirmDto confirmDto) {
        paymentService.confirm(ServiceEnum.RESERVATION, confirmDto);
    }

    @DltHandler
    public void dltHandler(ConsumerRecord<String, ConfirmDto> record) {
        ConfirmDto confirmDto = record.value();
        RefundDto refundDto = RefundDto.builder()
                .impUid(confirmDto.getImpUid())
                .merchantUid(confirmDto.getMerchantUid())
                .build();
        paymentService.refund(ServiceEnum.RESERVATION, refundDto);
    }
}
