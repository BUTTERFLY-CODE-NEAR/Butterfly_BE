package com.codenear.butterfly.notify.alarm.presentation;

import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.notify.alarm.application.AlarmService;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmCountResponseDTO;
import com.codenear.butterfly.notify.alarm.domain.dto.AlarmsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notify/alarm")
public class AlarmController implements AlarmControllerSwagger {

    private final AlarmService alarmService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAlarms(@AuthenticationPrincipal MemberDTO loginMember) {
        AlarmsResponse alarms = alarmService.getAlarmsByMemberId(loginMember.getId());
        return ResponseUtil.createSuccessResponse(alarms);
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseDTO> getUnreadAlarmCount(MemberDTO loginMember) {
        AlarmCountResponseDTO unreadAlarmCount = alarmService.getAlarmCountByMember(loginMember.getId());
        return ResponseUtil.createSuccessResponse(unreadAlarmCount);
    }
}
