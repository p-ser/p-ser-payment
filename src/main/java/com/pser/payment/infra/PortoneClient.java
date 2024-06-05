package com.pser.payment.infra;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.pser.payment.dto.PaymentDto;
import com.pser.payment.dto.PortoneResponse;
import com.pser.payment.dto.RefundDto;
import io.vavr.control.Try;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
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
        ResponseEntity<PortoneResponse<PaymentDto>> responseEntity = restTemplate.exchange(requestEntity,
                new ParameterizedTypeReference<>() {
                });
        return Optional.ofNullable(responseEntity.getBody())
                .orElseThrow()
                .getResponse();
    }

    private PaymentDto refund(RefundDto refundDto) {
        String url = env.getProperty("portone.refund-url", "");
        RequestEntity<RefundDto> requestEntity = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token))
                .body(refundDto);
        ResponseEntity<PortoneResponse<PaymentDto>> responseEntity = restTemplate.exchange(requestEntity,
                new ParameterizedTypeReference<>() {
                });
        return Optional.ofNullable(responseEntity.getBody())
                .map(PortoneResponse::getResponse)
                .orElse(
                        PaymentDto.builder()
                                .impUid(refundDto.getImpUid())
                                .merchantUid(refundDto.getMerchantUid())
                                .build()
                );
    }

    private void refreshToken() {
        String url = env.getProperty("portone.token-url", "");
        TokenRequest tokenRequest = TokenRequest.builder()
                .impKey(env.getProperty("portone.imp-key"))
                .impSecret(env.getProperty("portone.imp-secret"))
                .build();
        RequestEntity<TokenRequest> requestEntity = RequestEntity
                .post(url)
                .body(tokenRequest);

        ParameterizedTypeReference<PortoneResponse<TokenResponse>> typeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<PortoneResponse<TokenResponse>> response = restTemplate.exchange(requestEntity, typeReference);

        String token = Optional.ofNullable(response.getBody())
                .map(PortoneResponse::getResponse)
                .map(TokenResponse::getAccessToken)
                .orElseThrow();
        if (token.equals(this.token)) {
            return;
        }
        setToken(token);
    }

    private synchronized void setToken(String token) {
        this.token = token;
    }

    @Getter
    @Builder
    public static class TokenResponse {
        @JsonAlias("access_token")
        private String accessToken;

        @JsonAlias("now")
        private Integer now;

        @JsonAlias("expired_at")
        private Integer expiredAt;
    }

    @Builder
    public static class TokenRequest {
        @JsonAlias("imp_key")
        private String impKey;

        @JsonAlias("imp_secret")
        private String impSecret;

        @JsonGetter("imp_key")
        public String getImpKey() {
            return impKey;
        }

        @JsonGetter("imp_secret")
        public String getImpSecret() {
            return impSecret;
        }
    }
}
