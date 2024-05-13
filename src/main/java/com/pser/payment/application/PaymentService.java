package com.pser.payment.application;

import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.ConfirmDto;
import com.pser.payment.dto.RefundDto;
import com.pser.payment.exception.ValidationFailedException;
import com.pser.payment.infra.PortoneClient;
import com.pser.payment.infra.kafka.producer.ConfirmAwaitingRollbackProducer;
import com.pser.payment.infra.kafka.producer.ConfirmedProducer;
import com.pser.payment.infra.kafka.producer.RefundedProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ConfirmedProducer confirmedProducer;
    private final RefundedProducer refundedProducer;
    private final ConfirmAwaitingRollbackProducer confirmAwaitingRollbackProducer;
    private final PortoneClient portoneClient;

    public void confirm(ServiceEnum serviceEnum, ConfirmDto confirmDto) {
        String impUid = confirmDto.getImpUid();

        portoneClient.tryGetByImpUid(impUid)
                .map(paymentDto -> {
                    String status = paymentDto.getResponse().getStatus();
                    if (status == null || !status.equals("paid")) {
                        throw new ValidationFailedException();
                    }
                    return paymentDto;
                })
                .onSuccess((paymentDto) -> confirmedProducer.notifyConfirmed(serviceEnum, paymentDto))
                .recover(ValidationFailedException.class, e -> {
                    confirmAwaitingRollbackProducer.notifyRollback(serviceEnum, confirmDto.getMerchantUid());
                    return null;
                })
                .get();
    }

    public void refund(ServiceEnum serviceEnum, RefundDto refundDto) {
        portoneClient.tryRefund(refundDto)
                .onSuccess(paymentDto -> refundedProducer.notifyRefunded(serviceEnum, paymentDto))
                .get();
    }
}
