package com.snapfit.main.security.dto;


import com.snapfit.main.user.domain.SnapfitUser;
import lombok.Getter;

import java.util.Objects;

@Getter
public class RequestTokenInfo {

    private final Long userId;

    public RequestTokenInfo(Long userId) {
        this.userId = userId;
    }

    public RequestTokenInfo(SnapfitUser snapfitUser) {
        this.userId = snapfitUser.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestTokenInfo that = (RequestTokenInfo) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
