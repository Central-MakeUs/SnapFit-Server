package com.snapfit.main.post.domain.dto;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class Price {
    private int min;
    private int price;

    public Price(int min, int price) {
        if (min <= 0 || price <= 0) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        this.min = min;
        this.price = price;
    }
}
