package com.snapfit.main.user.presentation;

import com.snapfit.main.common.exception.ErrorCode;
import com.snapfit.main.security.JwtToken;
import com.snapfit.main.user.adapter.UserAdapter;
import com.snapfit.main.user.adapter.dto.SnapfitUserDto;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.domain.exception.UserErrorCode;
import com.snapfit.main.user.presentation.dto.RequestRefreshToken;
import com.snapfit.main.user.presentation.dto.SignUpDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final UserAdapter userAdapter;

    @PostMapping("/snapfit/signUp")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "소셜 회원가입", description = "소셜 토큰과 소셜 타입을 통해 회원가입을 할 수 있다. 헤더에는 소셜 access token을 넣어야 한다.")
    Mono<ResponseEntity<JwtToken>> signUp(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @RequestBody @Valid SignUpDto signUpDto) {

        return userAdapter.signUp(JwtToken.parseAccessTokenFromHeader(accessToken), signUpDto).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/login")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "소셜 로그인", description = "소셜 토큰과 소셜 타입을 통해 로그인을 할 수 있다. 헤더에는 소셜 access token을 넣어야 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = JwtToken.class))}),
    })
    Mono<ResponseEntity<JwtToken>> login(
            @Parameter(hidden = true)@RequestHeader("Authorization") String accessToken,
            @RequestParam("SocialType") SocialType socialType) {
        return userAdapter.login(JwtToken.parseAccessTokenFromHeader(accessToken), socialType).map(ResponseEntity::ok);
    }

    @GetMapping("/refresh/token")
    Mono<ResponseEntity<JwtToken>> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return userAdapter.refreshToken(refreshToken).map(ResponseEntity::ok);
    }

}
