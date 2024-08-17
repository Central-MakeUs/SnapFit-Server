package com.snapfit.main.user.presentation;

import com.snapfit.main.common.domain.location.Location;
import com.snapfit.main.security.JwtToken;
import com.snapfit.main.user.adapter.UserAdapter;
import com.snapfit.main.user.adapter.dto.SnapfitUserDto;
import com.snapfit.main.common.domain.vibe.Vibe;
import com.snapfit.main.user.domain.exception.UserErrorCode;
import com.snapfit.main.user.presentation.dto.InfoModifyDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    Mono<ResponseEntity<List<Vibe>>> vibes() {
        return userAdapter.findAllVibes()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/snapfit/locations")
    Mono<ResponseEntity<List<Location>>> locations() {
        return userAdapter.getLocations().map(ResponseEntity::ok);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "탈퇴", description = "회원 탈퇴 기능.")
    @DeleteMapping("/snapfit/user")
    Mono<ResponseEntity<Void>> leaveSnapfit(Authentication authentication) {
        return userAdapter.leaveSnapfit(Long.valueOf(authentication.getName()))
                .map(ResponseEntity::ok);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "수정", description = "닉네임 분위기 수정.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "409", description = "닉네임이 이미 존재하는 경우", content = {@Content(schema = @Schema(implementation = UserErrorCode.class))})
    })
    @PostMapping("/snapfit/user/info")
    Mono<ResponseEntity<SnapfitUserDto>> changeInfo(Authentication authentication, @Valid @RequestBody InfoModifyDto request) {
        return userAdapter.modifyUserInfo(Long.parseLong(authentication.getName()), request).map(ResponseEntity::ok);
    }

}
