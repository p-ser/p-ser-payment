package com.pser.payment.infra.kafka.producer;

import com.pser.payment.domain.ServiceEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidationRequiredRollbackProducer {
    private final KafkaTemplate<String, String> stringValueKafkaTemplate;

    public void notifyRollback(ServiceEnum serviceEnum, String merchantUid) {
        String topic = serviceEnum.getTopicPrefix() + ".payment-validation-required-rollback";
        stringValueKafkaTemplate.send(topic, merchantUid);
    }
}
