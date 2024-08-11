package com.snapfit.main.user.infra.apple.dto;

public record ApplePublicKey(String kty,
                             String kid,
                             String alg,
                             String n,
                             String e) {
}
