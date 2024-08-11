package com.snapfit.main.user.infra.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.user.domain.SocialInfo;
import com.snapfit.main.user.domain.SocialLogin;

import com.snapfit.main.user.domain.exception.UserErrorCode;
import com.snapfit.main.user.infra.PublicKeyGenerator;
import com.snapfit.main.user.infra.apple.dto.ApplePublicKeyResponse;
import com.snapfit.main.user.infra.apple.dto.AppleUserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.security.sasl.AuthenticationException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;

@Component
@Slf4j
public class AppleLogin implements SocialLogin {

    private final PublicKeyGenerator publicKeyGenerator;
    private final WebClient appleWebClient;
    private final ObjectMapper objectMapper;

    public AppleLogin(PublicKeyGenerator publicKeyGenerator, @Qualifier("appleWebClient") WebClient appleWebClient, ObjectMapper objectMapper) {
        this.publicKeyGenerator = publicKeyGenerator;
        this.appleWebClient = appleWebClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<? extends SocialInfo> getSocialInfo(String token) {
        return getAppleAccountId(token)
                .map(id -> AppleUserId.builder().id(id).build());
    }


    private Mono<ApplePublicKeyResponse> getApplePublicKeys() {
        return appleWebClient.get()
                .retrieve()
//                .onStatus(httpStatusCode -> !httpStatusCode.is2xxSuccessful(), clientResponse -> Mono.error(new ErrorResponse(UserErrorCode.INVALID_SOCIAL_TOKEN)))
                .bodyToMono(ApplePublicKeyResponse.class);
    }

    public Mono<String> getAppleAccountId(String identityToken) {

        return getApplePublicKeys()
                .flatMap(applePublicKeyResponse -> {
                    try {
                        String header = identityToken.split("\\.")[0];

                        Map<String, String> headers = objectMapper.readValue(decodeHeader(header), Map.class);

                        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeyResponse);
                        return Mono.just(getTokenClaims(identityToken, publicKey).getSubject());
                    } catch (Exception ex) {
                        log.error("[APPLE] {}", ex.getMessage());
                        return Mono.error(new ErrorResponse(UserErrorCode.INVALID_SOCIAL_TOKEN));
                    }
                });

    }

    public Claims getTokenClaims(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String decodeHeader(String token) {
        return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    }
}
