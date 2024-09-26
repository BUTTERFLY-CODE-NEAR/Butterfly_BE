package com.codenear.butterfly.product.domain;

import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.exception.MemberException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    ALL("전체"),
    FOOD("음식"),
    CLOTH("의류"),
    STATIONERY("문구");

    private final String value;

    public static Category fromValue(String value) {
        for (Category category : Category.values()) {
            if (category.getValue().equals(value)) {
                return category;
            }
        }

        throw new MemberException(ErrorCode.SERVER_ERROR, null);
    }
}
