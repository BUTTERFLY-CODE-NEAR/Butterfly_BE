package com.codenear.butterfly.admin;

import com.codenear.butterfly.admin.products.dto.ScheduleUpdateRequest;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.product.domain.dto.MealSchedulerInfoDTO;
import com.codenear.butterfly.product.util.SBProductScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final SBProductScheduler sbProductScheduler;

    @GetMapping
    public String showAdminPage(Model model) {
        MealSchedulerInfoDTO scheduleInfo = sbProductScheduler.getCurrentScheduleInfo();
        model.addAttribute("scheduleInfo", scheduleInfo);
        return "adminDashboard";
    }

    /**
     * 현재 스케줄러 상태 조회
     */
    @GetMapping("/scheduler/status")
    public ResponseEntity<ResponseDTO> getSchedulerStatus() {
        MealSchedulerInfoDTO scheduleInfo = sbProductScheduler.getCurrentScheduleInfo();
        return ResponseUtil.createSuccessResponse("스케줄러 상태 조회 성공", scheduleInfo);
    }

    /**
     * 점심 시작 스케줄러 변경
     */
    @PostMapping("/schedule/lunch")
    public String updateLunchSchedule(
            @ModelAttribute ScheduleUpdateRequest scheduleUpdateRequest,
            RedirectAttributes redirectAttributes) {
        try {
            sbProductScheduler.updateLunchSchedule(scheduleUpdateRequest);
            redirectAttributes.addFlashAttribute("message", "점심 스케줄이 성공적으로 변경되었습니다:");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "잘못된 cron 표현식입니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin";
    }

    /**
     * 저녁 스케줄 변경
     */
    @PostMapping("/schedule/dinner")
    public String updateDinnerSchedule(
            @ModelAttribute ScheduleUpdateRequest scheduleUpdateRequest,
            RedirectAttributes redirectAttributes) {
        try {
            sbProductScheduler.updateDinnerSchedule(scheduleUpdateRequest);
            redirectAttributes.addFlashAttribute("message", "저녁 스케줄이 성공적으로 변경되었습니다:");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "잘못된 cron 표현식입니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin";
    }
}
