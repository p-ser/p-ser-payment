package com.pser.payment.application;

import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.ConfirmDto;
import com.pser.payment.dto.RefundDto;
import com.pser.payment.infra.IdempotencyManager;
import com.pser.payment.infra.PortoneClient;
import com.pser.payment.producer.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentProducer paymentProducer;
    private final PortoneClient portoneClient;
    private final IdempotencyManager idempotencyManager;

    public void confirm(ServiceEnum serviceEnum, ConfirmDto confirmDto) {
        String impUid = confirmDto.getImpUid();
        String idempotencyKey = "confirmed" + impUid;

        if (!idempotencyManager.mark(idempotencyKey)) {
            return;
        }
        portoneClient.tryGetByImpUid(impUid)
                .onSuccess((paymentDto) -> {
                    paymentProducer.notifyConfirmed(serviceEnum, paymentDto);
                })
                .onFailure((e) -> {
                    idempotencyManager.unmark(idempotencyKey);
                    paymentProducer.rollbackConfirmAwaiting(serviceEnum, impUid);
                })
                .get();
    }

    public void refund(ServiceEnum serviceEnum, RefundDto refundDto) {
        String impUid = refundDto.getImpUid();
        String idempotencyKey = "confirmed" + impUid;

        if (!idempotencyManager.mark(idempotencyKey)) {
            return;
        }
        portoneClient.tryRefund(refundDto)
                .onSuccess(paymentDto -> paymentProducer.notifyRefunded(serviceEnum, paymentDto))
                .onFailure((e) -> {
                    idempotencyManager.unmark(idempotencyKey);
                    paymentProducer.rollbackRefundAwaiting(serviceEnum, impUid);
                })
                .get();
    }

    public void rollbackConfirmed(ServiceEnum serviceEnum, String impUid) {
        String idempotencyKey = "confirmed" + impUid;

        idempotencyManager.unmark(idempotencyKey);
        paymentProducer.rollbackConfirmAwaiting(serviceEnum, impUid);
    }
}
