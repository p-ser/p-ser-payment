package com.pser.payment.application;

import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.PaymentDto;
import com.pser.payment.dto.RefundDto;
import com.pser.payment.exception.ValidationFailedException;
import com.pser.payment.infra.PortoneClient;
import com.pser.payment.infra.kafka.producer.PaymentValidationRequiredRollbackProducer;
import com.pser.payment.infra.kafka.producer.PaymentValidationCheckedProducer;
import com.pser.payment.infra.kafka.producer.RefundCheckedProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentValidationCheckedProducer paymentValidationCheckedProducer;
    private final RefundCheckedProducer refundCheckedProducer;
    private final PaymentValidationRequiredRollbackProducer paymentValidationRequiredRollbackProducer;
    private final PortoneClient portoneClient;

    public void validatePayment(ServiceEnum serviceEnum, PaymentDto paymentDto) {
        String impUid = paymentDto.getImpUid();

        portoneClient.tryGetByImpUid(impUid)
                .map(dto -> {
                    String status = dto.getStatus();
                    if (status == null || !status.equals("paid")) {
                        throw new ValidationFailedException();
                    }
                    return dto;
                })
                .onSuccess((dto) -> paymentValidationCheckedProducer.notifyPaymentValidationChecked(serviceEnum, dto))
                .recover(ValidationFailedException.class, e -> {
                    paymentValidationRequiredRollbackProducer.notifyRollback(serviceEnum, paymentDto.getMerchantUid());
                    return null;
                })
                .get();
    }

    public void refund(ServiceEnum serviceEnum, RefundDto refundDto) {
        portoneClient.tryRefund(refundDto)
                .onSuccess(paymentDto -> refundCheckedProducer.notifyRefundChecked(serviceEnum, paymentDto))
                .get();
    }
}
