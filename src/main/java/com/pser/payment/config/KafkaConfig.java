package com.pser.payment.config;

import com.pser.payment.consumer.ConfirmDtoDeserializer;
import com.pser.payment.consumer.RefundDtoDeserializer;
import com.pser.payment.dto.ConfirmDto;
import com.pser.payment.dto.PaymentDto;
import com.pser.payment.dto.RefundDto;
import com.pser.payment.producer.PaymentDtoSerializer;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final Environment env;

    @Bean
    public ProducerFactory<String, PaymentDto> paymentDtoValueProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("kafka.bootstrap-servers"));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PaymentDtoSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, PaymentDto> paymentDtoValueKafkaTemplate() {
        return new KafkaTemplate<>(paymentDtoValueProducerFactory());
    }

    @Bean
    public ProducerFactory<String, String> stringValueProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("kafka.bootstrap-servers"));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> stringValueKafkaTemplate() {
        return new KafkaTemplate<>(stringValueProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, ConfirmDto> confirmDtoValueConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("kafka.bootstrap-servers"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("kafka.consumer-group-id"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ConfirmDtoDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConfirmDto> confirmDtoValueListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConfirmDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(confirmDtoValueConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, RefundDto> refundDtoValueConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("kafka.bootstrap-servers"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("kafka.consumer-group-id"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, RefundDtoDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RefundDto> refundDtoValueListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RefundDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(refundDtoValueConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> stringValueConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("kafka.bootstrap-servers"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("kafka.consumer-group-id"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringValueListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringValueConsumerFactory());

        return factory;
    }
}
