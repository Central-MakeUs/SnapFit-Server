package com.snapfit.main.security;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import com.snapfit.main.security.dto.RequestTokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenProvider {

    private final Key key;

    private final RefreshTokenRepository refreshTokenRepository;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String key, RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.access-expire}") long accessTokenExpireTime,
            @Value("${jwt.refresh-expire}") long refreshTokenExpireTime) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenRepository = refreshTokenRepository;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }


    public Mono<JwtToken> createToken(RequestTokenInfo requestTokenInfo) {
        String accessToken = createToken(requestTokenInfo, accessTokenExpireTime);
        String refreshToken = createToken(requestTokenInfo, refreshTokenExpireTime);

        return saveRefreshToken(requestTokenInfo, refreshToken)
                .then(Mono.just(JwtToken.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build()));


    }

    public Mono<JwtToken> refreshToken(String refreshToken) {

        return Mono.just(validateToken(refreshToken))
                .filter(valid -> valid)
                .switchIfEmpty(Mono.error(new ErrorResponse(CommonErrorCode.INVALID_TOKEN)))
                .flatMap(valid -> refreshTokenRepository.findByRefreshToken(refreshToken))
                .switchIfEmpty(Mono.error(new ErrorResponse(CommonErrorCode.INVALID_TOKEN)))
                .flatMap(refreshTokenInfo -> createToken(new RequestTokenInfo(refreshTokenInfo.getUserId())));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        } catch (Exception e) {
            log.info("invalid jwt");
        }

        return false;
    }

    public Mono<Void> logOut(Long userId, String refreshToken) {
        if (!validateToken(refreshToken) || !getUserId(refreshToken).equals(userId)) {
            return Mono.error(new ErrorResponse(CommonErrorCode.INVALID_AUTH));
        }

        return refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    private String createToken(RequestTokenInfo requestTokenInfo, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("userId", requestTokenInfo.getUserId());
        claims.put("role", List.of("ROLE_USER"));
        //TODO 유저 권한 등 더 추가로 넣어야 함.

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime validTime = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(validTime.toInstant()))
                .signWith(key)
                .compact();
    }

    private Mono<RefreshTokenInfo> saveRefreshToken(RequestTokenInfo requestTokenInfo, String refreshToken) {
        return refreshTokenRepository.save(RefreshTokenInfo.builder()
                        .userId(requestTokenInfo.getUserId())
                        .refreshToken(refreshToken)
                .build());
    }

}
