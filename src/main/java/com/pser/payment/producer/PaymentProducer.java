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

    public void notifyConfirmed(ServiceEnum webhookType, PaymentDto paymentDto) {
        String topic = webhookType.getTopicPrefix() + "-confirmed";
        paymentDtoValueKafkaTemplate.send(topic, paymentDto);
    }

    public void notifyRefunded(ServiceEnum webhookType, PaymentDto paymentDto) {
        String topic = webhookType.getTopicPrefix() + "-refunded";
        paymentDtoValueKafkaTemplate.send(topic, paymentDto);
    }
}
