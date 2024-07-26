package com.snapfit.main.user.presentation;

import com.snapfit.main.user.adapter.UserAdapter;
import com.snapfit.main.user.adapter.dto.SnapfitUserDto;
import com.snapfit.main.user.domain.enums.VibeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor

public class UserController {

    private final UserAdapter userAdapter;

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/snapfit/user")
    Mono<ResponseEntity<SnapfitUserDto>> info(Authentication authentication){
        long userId = Long.parseLong(authentication.getName());
        return userAdapter.getSnapfitUser(userId).map(ResponseEntity::ok);
    }

    //TODO 추후 클래스 변경 필요
    @GetMapping("/snapfit/vibes")
    Mono<ResponseEntity<List<String>>> vibes() {
        return Mono.just(VibeType.values())
                .map(vibeTypes -> Arrays.stream(vibeTypes).map(VibeType::getVibe).toList())
                .map(ResponseEntity::ok);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "탈퇴", description = "회원 탈퇴 기능.")
    @DeleteMapping("/user")
    Mono<ResponseEntity<Void>> leaveSnapfit(Authentication authentication) {
        return userAdapter.leaveSnapfit(Long.valueOf(authentication.getName()))
                .map(ResponseEntity::ok);
    }

}
