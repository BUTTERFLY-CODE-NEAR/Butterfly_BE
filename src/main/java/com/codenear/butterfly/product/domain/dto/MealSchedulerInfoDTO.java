package com.codenear.butterfly.product.domain.dto;

import lombok.Builder;

@Builder
public record MealSchedulerInfoDTO(String lunchStartCron,
                                   String dinnerStartCron,
                                   String lunchEndCron,
                                   String dinnerEndCron,
                                   boolean lunchStartSchedulerActive,
                                   boolean dinnerStartSchedulerActive,
                                   boolean lunchEndSchedulerActive,
                                   boolean dinnerEndSchedulerActive) {
}
