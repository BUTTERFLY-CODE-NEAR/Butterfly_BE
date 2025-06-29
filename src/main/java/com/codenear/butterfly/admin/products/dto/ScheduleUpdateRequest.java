package com.codenear.butterfly.admin.products.dto;

public record ScheduleUpdateRequest(String cronStartExpression,
                                    String cronEndExpression) {
}
