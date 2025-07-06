package com.codenear.butterfly.member.util;

import com.codenear.butterfly.global.dto.DebeziumChangeEventDTO;
import com.codenear.butterfly.member.application.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberDebeziumEventListener {
    private final MemberService memberService;

    /**
     * 'member' 테이블에 대한 DmlChangeEvent만 수신하여 처리
     *
     * @param event 발생한 DML 변경 이벤트
     */
    @EventListener(condition = "#event.tableName == 'member'")
    public void handleMemberDmlChange(DebeziumChangeEventDTO event) {
        String operationType = event.getOperationType();
        JsonNode dataPayload = event.getDataPayload();

        log.info("[회원 테이블] DML 변경 이벤트 수신 : 작업={}, 데이터={}", operationType, dataPayload);

        switch (operationType) {
            case "c" -> handleInsert(event.getTableName(), dataPayload);
            case "u" -> handleUpdate(event.getTableName(), dataPayload);
            case "d" -> handleDelete(event.getTableName(), dataPayload);
            default -> log.warn("처리되지 않은 DML 작업 유형: {}", operationType);
        }
    }

    /**
     * INSERT(생성) 이벤트를 처리하는 메서드.
     *
     * @param table       변경이 발생한 테이블 이름
     * @param dataPayload 삽입된 레코드 데이터
     */
    private void handleInsert(String table, JsonNode dataPayload) {
        log.debug("테이블 '{}'에 새 레코드 삽입됨: {}", table, dataPayload);
        // TODO: INSERT에 대한 비즈니스 로직
    }

    /**
     * UPDATE(수정) 이벤트를 처리하는 메서드.
     *
     * @param table       변경이 발생한 테이블 이름
     * @param dataPayload 수정된 레코드 데이터
     */
    private void handleUpdate(String table, JsonNode dataPayload) {
        log.debug("테이블 '{}'의 레코드 업데이트됨: {}", table, dataPayload);

        Long memberId = dataPayload.get("id").asLong();
        memberService.evictMemberCache(memberId);
    }

    /**
     * DELETE(삭제) 이벤트를 처리하는 메서드.
     *
     * @param table       변경이 발생한 테이블 이름
     * @param dataPayload 삭제된 레코드 데이터 (삭제 전 데이터)
     */
    private void handleDelete(String table, JsonNode dataPayload) {
        log.debug("테이블 '{}'의 레코드 삭제됨: {}", table, dataPayload);
        // TODO: DELETE에 대한비즈니스 로직
    }
}

