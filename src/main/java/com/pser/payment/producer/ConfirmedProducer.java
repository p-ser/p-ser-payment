package com.pser.payment.producer;

import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmedProducer {
    private final KafkaTemplate<String, PaymentDto> paymentDtoValueKafkaTemplate;

    public void notifyConfirmed(ServiceEnum serviceEnum, PaymentDto paymentDto) {
        String topic = serviceEnum.getTopicPrefix() + ".confirmed";
        paymentDtoValueKafkaTemplate.send(topic, paymentDto);
    }
}
