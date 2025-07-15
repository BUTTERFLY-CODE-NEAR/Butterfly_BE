package com.codenear.butterfly.product.application;

import com.codenear.butterfly.product.domain.dto.MealScheduleTimeDTO;
import com.codenear.butterfly.product.domain.dto.MealSchedulerInfoDTO;
import com.codenear.butterfly.product.util.SBProductScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealScheduleService {
    private final SBProductScheduler sbProductScheduler;

    /**
     * 현재 스케줄러 시간 정보
     *
     * @return 식사 시간 정보
     */
    public MealScheduleTimeDTO getMealScheduleTime() {
        MealSchedulerInfoDTO mealSchedule = sbProductScheduler.getCurrentScheduleInfo();

        return MealScheduleTimeDTO.builder()
                .lunchStartHour(getCronHour(mealSchedule.lunchStartCron()))
                .lunchStartMinute(getCronMinute(mealSchedule.lunchStartCron()))
                .lunchEndHour(getCronHour(mealSchedule.lunchEndCron()))
                .lunchEndMinute(getCronMinute(mealSchedule.lunchEndCron()))
                .dinnerStartHour(getCronHour(mealSchedule.dinnerStartCron()))
                .dinnerStartMinute(getCronMinute(mealSchedule.dinnerStartCron()))
                .dinnerEndHour(getCronHour(mealSchedule.dinnerEndCron()))
                .dinnerEndMinute(getCronMinute(mealSchedule.dinnerEndCron()))
                .build();
    }

    // Cron 표현식에서 시간(hour) 추출
    private String getCronHour(String cronExpression) {
        return cronExpression.split(" ")[2];
    }

    // Cron 표현식에서 분(minute) 추출
    private String getCronMinute(String cronExpression) {
        return cronExpression.split(" ")[1];
    }
}
