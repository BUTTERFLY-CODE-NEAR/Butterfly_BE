package com.codenear.butterfly.notify.alarm.domain.dto;

import com.codenear.butterfly.notify.alarm.domain.Restock;

public record RestockResponseDTO(Long memberId,
                                 Long productId) {

    public static RestockResponseDTO from(Restock restock) {
        return new RestockResponseDTO(restock.getMember().getId(), restock.getProduct().getId());
    }
}
