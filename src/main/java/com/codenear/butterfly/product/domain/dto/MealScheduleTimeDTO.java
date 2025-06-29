package com.codenear.butterfly.product.domain.dto;

import lombok.Builder;

@Builder
public record MealScheduleTimeDTO(String lunchStartHour,
                                  String lunchStartMinute,
                                  String lunchEndHour,
                                  String lunchEndMinute,
                                  String dinnerStartHour,
                                  String dinnerStartMinute,
                                  String dinnerEndHour,
                                  String dinnerEndMinute) {
}
