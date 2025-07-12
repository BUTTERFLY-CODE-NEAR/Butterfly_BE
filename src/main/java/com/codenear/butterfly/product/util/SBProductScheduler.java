package com.codenear.butterfly.product.util;

import com.codenear.butterfly.admin.products.application.AdminProductService;
import com.codenear.butterfly.admin.products.dto.ScheduleUpdateRequest;
import com.codenear.butterfly.payment.domain.repository.PaymentRedisRepository;
import com.codenear.butterfly.product.domain.SBMealType;
import com.codenear.butterfly.product.domain.SmallBusinessProduct;
import com.codenear.butterfly.product.domain.dto.MealSchedulerInfoDTO;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import com.codenear.butterfly.product.domain.repository.ProductRedisRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
public class SBProductScheduler {
    // 기본 cron 표현식 (초기값)
    private static final String LUNCH_START_KEY = "lunchStart";
    private static final String LUNCH_END_KEY = "lunchEnd";
    private static final String DINNER_START_KEY = "dinnerStart";
    private static final String DINNER_END_KEY = "dinnerEnd";
    private final ProductRedisRepository productRedisRepository;
    private final PaymentRedisRepository paymentRedisRepository;
    private final ProductInventoryRepository productRepository;
    private final AdminProductService adminProductService;
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    private final Map<String, String> cronExpressions = new HashMap<>();

    @Autowired
    public SBProductScheduler(ProductRedisRepository productRedisRepository,
                              PaymentRedisRepository paymentRedisRepository,
                              ProductInventoryRepository productRepository,
                              AdminProductService adminProductService) {
        this.productRedisRepository = productRedisRepository;
        this.paymentRedisRepository = paymentRedisRepository;
        this.productRepository = productRepository;
        this.adminProductService = adminProductService;
        this.taskScheduler = createDedicatedTaskScheduler();
        // 기본값 초기화
        cronExpressions.put(LUNCH_START_KEY, "0 0 0 * * *"); // 점심 시작 (00:00)
        cronExpressions.put(LUNCH_END_KEY, "0 30 11 * * *"); // 점심 종료 (11:30)
        cronExpressions.put(DINNER_START_KEY, "0 30 12 * * *"); // 저녁 시작 (12:30)
        cronExpressions.put(DINNER_END_KEY, "0 30 17 * * *"); // 저녁 종료 (17:30)
    }

    @PostConstruct
    public void initializeSchedulers() {
        log.info("동적 스케줄러 초기화 시작");
        startScheduler(LUNCH_START_KEY, () -> updateProducts(SBMealType.LUNCH), cronExpressions.get(LUNCH_START_KEY));
        startScheduler(LUNCH_END_KEY, () -> clearProducts(SBMealType.LUNCH), cronExpressions.get(LUNCH_END_KEY));
        startScheduler(DINNER_START_KEY, () -> updateProducts(SBMealType.DINNER), cronExpressions.get(DINNER_START_KEY));
        startScheduler(DINNER_END_KEY, () -> clearProducts(SBMealType.DINNER), cronExpressions.get(DINNER_END_KEY));
    }

    @PreDestroy
    public void destroySchedulers() {
        log.info("스케줄러 종료 중...");
        stopScheduler(LUNCH_START_KEY);
        stopScheduler(LUNCH_END_KEY);
        stopScheduler(DINNER_START_KEY);
        stopScheduler(DINNER_END_KEY);
        if (taskScheduler instanceof ThreadPoolTaskScheduler) {
            ((ThreadPoolTaskScheduler) taskScheduler).shutdown();
        }
    }

    /**
     * 점심 스케줄 동적 변경
     *
     * @param request 변경 시간을 담은 DTO (cron)
     */
    public void updateLunchSchedule(ScheduleUpdateRequest request) {
        updateSchedule(request, "점심", LUNCH_START_KEY, LUNCH_END_KEY,
                () -> updateProducts(SBMealType.LUNCH), () -> clearProducts(SBMealType.LUNCH));
    }

    /**
     * 저녁 스케줄 동적 변경
     *
     * @param request 변경 시간을 담은 DTO (cron)
     */
    public void updateDinnerSchedule(ScheduleUpdateRequest request) {
        updateSchedule(request, "저녁", DINNER_START_KEY, DINNER_END_KEY,
                () -> updateProducts(SBMealType.DINNER), () -> clearProducts(SBMealType.DINNER));
    }

    /**
     * 현재 스케줄 정보 조회
     */
    public MealSchedulerInfoDTO getCurrentScheduleInfo() {
        return MealSchedulerInfoDTO.builder()
                .lunchStartCron(cronExpressions.get(LUNCH_START_KEY))
                .lunchEndCron(cronExpressions.get(LUNCH_END_KEY))
                .dinnerStartCron(cronExpressions.get(DINNER_START_KEY))
                .dinnerEndCron(cronExpressions.get(DINNER_END_KEY))
                .lunchStartSchedulerActive(isSchedulerActive(LUNCH_START_KEY))
                .lunchEndSchedulerActive(isSchedulerActive(LUNCH_END_KEY))
                .dinnerStartSchedulerActive(isSchedulerActive(DINNER_START_KEY))
                .dinnerEndSchedulerActive(isSchedulerActive(DINNER_END_KEY))
                .build();
    }

    /**
     * 공통 스케줄 업데이트 로직
     */
    private void updateSchedule(ScheduleUpdateRequest request, String mealType,
                                String startKey, String endKey,
                                Runnable startTask, Runnable endTask) {
        try {
            // Cron 표현식 유효성 검증
            new CronTrigger(request.cronStartExpression());
            new CronTrigger(request.cronEndExpression());

            // 시간 순서 검증
            validateScheduleTimes(request, mealType);

            // 스케줄 업데이트
            cronExpressions.put(startKey, request.cronStartExpression());
            cronExpressions.put(endKey, request.cronEndExpression());
            startScheduler(startKey, startTask, cronExpressions.get(startKey));
            startScheduler(endKey, endTask, cronExpressions.get(endKey));

            log.info("{} 스케줄 업데이트 완료: start={}, end={}", mealType,
                    cronExpressions.get(startKey), cronExpressions.get(endKey));
        } catch (IllegalArgumentException e) {
            log.error("잘못된 {} cron 표현식: {}", mealType, e.getMessage());
            throw new IllegalArgumentException("잘못된 cron 표현식입니다: " + e.getMessage());
        }
    }

    /**
     * 스케줄러 순서 검증: 시작 시간이 종료 시간보다 빠르도록
     */
    private void validateScheduleTimes(ScheduleUpdateRequest request, String mealType) {
        int startHour = Integer.parseInt(request.cronStartExpression().split(" ")[2]);
        int startMinute = Integer.parseInt(request.cronStartExpression().split(" ")[1]);
        int endHour = Integer.parseInt(request.cronEndExpression().split(" ")[2]);
        int endMinute = Integer.parseInt(request.cronEndExpression().split(" ")[1]);

        if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
            throw new IllegalArgumentException(mealType + " 시작 시간은 종료 시간보다 빠르거나 같을 수 없습니다.");
        }
    }

    /**
     * SBProductScheduler 전용 TaskScheduler
     */
    private TaskScheduler createDedicatedTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4); // 점심 시작/종료, 저녁 시작/종료
        scheduler.setThreadNamePrefix("sb-product-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(20);
        scheduler.initialize();
        return scheduler;
    }

    /**
     * 공통 스케줄러 시작
     */
    private void startScheduler(String taskKey, Runnable task, String cronExpression) {
        stopScheduler(taskKey); // 기존 스케줄러 중지
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(task, new CronTrigger(cronExpression));
        scheduledTasks.put(taskKey, scheduledTask);
        log.info("{} 스케줄러 시작 - cron: {}", taskKey, cronExpression);
    }

    /**
     * 공통 스케줄러 중지
     */
    private void stopScheduler(String taskKey) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(taskKey);
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
            log.info("{} 스케줄러 중지됨", taskKey);
        }
    }

    /**
     * 스케줄러 활성 상태 확인
     */
    private boolean isSchedulerActive(String taskKey) {
        ScheduledFuture<?> task = scheduledTasks.get(taskKey);
        return task != null && !task.isCancelled();
    }

    /**
     * 상품 업데이트
     */
    private void updateProducts(SBMealType mealType) {
        try {
            log.info("{} 상품 업데이트 스케줄러 실행: {}", mealType.name(), LocalDate.now());
            productRedisRepository.saveCurrentSmallBusinessProductIds(mealType);
            List<SmallBusinessProduct> products = getSBProduct(mealType);

            products.forEach(product -> paymentRedisRepository.saveStockQuantity(product.getProductName(), product.getStockQuantity()));
        } catch (Exception e) {
            log.error("{} 상품 업데이트 중 오류 발생", mealType.name(), e);
        }
    }

    /**
     * 상품 캐시 삭제
     */
    private void clearProducts(SBMealType mealType) {
        try {
            log.info("{} 상품 삭제 스케줄러 실행: {}", mealType.name(), LocalDate.now());
            productRedisRepository.clearCurrentSmallBusinessProductIds();

            List<SmallBusinessProduct> products = getSBProduct(mealType);

            products.forEach(product -> {
                product.resetQuantity();
                paymentRedisRepository.removeRemainderProduct(product.getProductName());
                productRepository.save(product);
            });
        } catch (Exception e) {
            log.error("{} 상품 캐시 삭제 중 오류 발생", mealType.name(), e);
        }
    }

    private List<SmallBusinessProduct> getSBProduct(SBMealType mealType) {
        List<Long> productIds = adminProductService.loadSmallBusinessProductsByMealType(mealType);
        return productRepository.findByIdIn(productIds);
    }
}