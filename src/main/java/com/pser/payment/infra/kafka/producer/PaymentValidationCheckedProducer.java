package com.pser.payment.infra.kafka.producer;

import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidationCheckedProducer {
    private final KafkaTemplate<String, PaymentDto> paymentDtoValueKafkaTemplate;

    public void notifyPaymentValidationChecked(ServiceEnum serviceEnum, PaymentDto paymentDto) {
        String topic = serviceEnum.getTopicPrefix() + ".payment-validation-checked";
        paymentDtoValueKafkaTemplate.send(topic, paymentDto);
    }
}
