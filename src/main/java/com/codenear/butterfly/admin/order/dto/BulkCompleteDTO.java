package com.codenear.butterfly.admin.order.dto;

import com.codenear.butterfly.payment.domain.dto.OrderStatus;

import java.util.List;

public record BulkCompleteDTO(List<Long> orderIds,
                              OrderStatus status) {
}
