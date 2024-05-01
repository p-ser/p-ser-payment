package com.pser.payment.producer;

import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {
    private final KafkaTemplate<String, PaymentDto> paymentDtoValueKafkaTemplate;
    private final KafkaTemplate<String, String> stringValueKafkaTemplate;

    public void notifyConfirmed(ServiceEnum webhookType, PaymentDto paymentDto) {
        String topic = webhookType.getTopicPrefix() + "-confirmed";
        paymentDtoValueKafkaTemplate.send(topic, paymentDto);
    }

    public void rollbackConfirmAwaiting(ServiceEnum webhookType, String impUid) {
        String topic = webhookType.getTopicPrefix() + "-confirm-awaiting-rollback";
        stringValueKafkaTemplate.send(topic, impUid);
    }

    public void notifyRefunded(ServiceEnum webhookType, PaymentDto paymentDto) {
        String topic = webhookType.getTopicPrefix() + "-refunded";
        paymentDtoValueKafkaTemplate.send(topic, paymentDto);
    }

    public void rollbackRefundAwaiting(ServiceEnum webhookType, String impUid) {
        String topic = webhookType.getTopicPrefix() + "-refund-awaiting-rollback";
        stringValueKafkaTemplate.send(topic, impUid);
    }
}
