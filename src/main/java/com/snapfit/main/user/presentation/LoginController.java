package com.snapfit.main.user.presentation;

import com.snapfit.main.security.JwtToken;
import com.snapfit.main.user.adapter.UserAdapter;
import com.snapfit.main.user.presentation.dto.SignUpDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final UserAdapter userAdapter;

    @PostMapping("/signUp")
    Mono<ResponseEntity<JwtToken>> signUp(@RequestHeader("accessToken") String accessToken, @RequestBody @Valid SignUpDto signUpDto) {

        return userAdapter.signUp(accessToken, signUpDto).map(ResponseEntity::ok);
    }

    @GetMapping("/login")
    Mono<ResponseEntity<JwtToken>> login(@RequestHeader("accessToken") String accessToken, @RequestHeader("device") String loginDevice) {
        return Mono.empty();
    }

}
