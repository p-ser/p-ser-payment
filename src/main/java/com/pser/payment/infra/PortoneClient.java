package com.pser.payment.infra;

import com.pser.payment.dto.PaymentDto;
import com.pser.payment.dto.RefundDto;
import io.vavr.control.Try;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortoneClient {
    private final RestTemplate restTemplate;
    private final Environment env;
    private String token;

    public Try<PaymentDto> tryGetByImpUid(String impUid) {
        return Try.of(() -> getByImpUid(impUid))
                .recover(Unauthorized.class, (e) -> {
                    refreshToken();
                    return getByImpUid(impUid);
                });
    }

    public Try<PaymentDto> tryRefund(RefundDto refundDto) {
        return Try.of(() -> refund(refundDto))
                .recover(Unauthorized.class, (e) -> {
                    refreshToken();
                    return refund(refundDto);
                })
                .onFailure(Exception.class, (e) -> log.error("환불 프로세스 중 오류: " + e.getMessage()));
    }

    private PaymentDto getByImpUid(String impUid) {
        String url = env.getProperty("portone.payment-url", "");
        RequestEntity<Void> requestEntity = RequestEntity
                .get("%s/%s".formatted(url, impUid))
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token))
                .build();
        ResponseEntity<PaymentDto> responseEntity = restTemplate.exchange(requestEntity, PaymentDto.class);
        return responseEntity.getBody();
    }

    private PaymentDto refund(RefundDto refundDto) {
        String url = env.getProperty("portone.refund-url", "");
        RequestEntity<RefundDto> requestEntity = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token))
                .body(refundDto);
        ResponseEntity<PaymentDto> responseEntity = restTemplate.exchange(requestEntity, PaymentDto.class);
        return responseEntity.getBody();
    }

    private String refreshToken() {
        String url = env.getProperty("portone.token-url", "");
        Map<String, String> requestBody = Map.of(
                "imp_key", env.getProperty("portone.imp-key", ""),
                "imp_secret", env.getProperty("portone.imp-secret", "")
        );
        Map<String, String> response = (Map) restTemplate.postForObject(url, requestBody, Map.class).get("response");
        String token = response.get("access_token");
        if (token.equals(this.token)) {
            return this.token;
        }
        setToken(token);
        return this.token;
    }

    private synchronized void setToken(String token) {
        this.token = token;
    }
}
