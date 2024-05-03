package com.pser.payment.application;

import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.ConfirmDto;
import com.pser.payment.dto.RefundDto;
import com.pser.payment.infra.PortoneClient;
import com.pser.payment.producer.ConfirmedProducer;
import com.pser.payment.producer.RefundedProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ConfirmedProducer confirmedProducer;
    private final RefundedProducer refundedProducer;
    private final PortoneClient portoneClient;

    public void confirm(ServiceEnum serviceEnum, ConfirmDto confirmDto) {
        String impUid = confirmDto.getImpUid();

        portoneClient.tryGetByImpUid(impUid)
                .onSuccess((paymentDto) -> confirmedProducer.notifyConfirmed(serviceEnum, paymentDto))
                .get();
    }

    public void refund(ServiceEnum serviceEnum, RefundDto refundDto) {
        portoneClient.tryRefund(refundDto)
                .onSuccess(paymentDto -> refundedProducer.notifyRefunded(serviceEnum, paymentDto))
                .get();
    }
}
