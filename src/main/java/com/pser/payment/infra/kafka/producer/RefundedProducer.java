package com.pser.payment.infra.kafka.producer;

import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundedProducer {
    private final KafkaTemplate<String, PaymentDto> paymentDtoValueKafkaTemplate;

    public void notifyRefunded(ServiceEnum serviceEnum, PaymentDto paymentDto) {
        String topic = serviceEnum.getTopicPrefix() + ".refunded";
        paymentDtoValueKafkaTemplate.send(topic, paymentDto);
    }
}
