package com.pser.payment.api;

import com.pser.payment.application.PaymentService;
import com.pser.payment.common.response.ApiResponse;
import com.pser.payment.domain.ServiceEnum;
import com.pser.payment.dto.ConfirmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentApi {
    private final PaymentService paymentService;

    @PostMapping("/webhooks/{webhookType}")
    public ResponseEntity<ApiResponse<Void>> post(@PathVariable ServiceEnum webhookType,
                                                  @RequestBody ConfirmDto request) {
        paymentService.confirm(webhookType, request);
        return ResponseEntity.noContent().build();
    }
}
