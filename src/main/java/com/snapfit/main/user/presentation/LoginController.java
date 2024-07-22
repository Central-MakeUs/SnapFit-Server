package com.snapfit.main.user.presentation;

import com.snapfit.main.security.JwtToken;
import com.snapfit.main.user.adapter.UserAdapter;
import com.snapfit.main.user.adapter.dto.SnapfitUserDto;
import com.snapfit.main.user.domain.enums.SocialType;
import com.snapfit.main.user.presentation.dto.SignUpDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class LoginController {
    private final UserAdapter userAdapter;

    @PostMapping("/snapfit/signUp")
    Mono<ResponseEntity<JwtToken>> signUp(
            @Parameter(hidden = true) @RequestHeader("Authorization") String accessToken,
            @RequestBody @Valid SignUpDto signUpDto) {

        return userAdapter.signUp(JwtToken.parseAccessTokenFromHeader(accessToken), signUpDto).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/login")
    Mono<ResponseEntity<JwtToken>> login(
            @Parameter(hidden = true)@RequestHeader("Authorization") String accessToken,
            @RequestParam("SocialType") String socialType) {
        return userAdapter.login(JwtToken.parseAccessTokenFromHeader(accessToken), SocialType.findBySocial(socialType)).map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/user")
    Mono<ResponseEntity<SnapfitUserDto>> info(Authentication authentication){
        long userId = Long.parseLong(authentication.getName());
        return userAdapter.getSnapfitUser(userId).map(ResponseEntity::ok);
    }

}
