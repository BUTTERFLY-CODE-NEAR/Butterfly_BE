package com.codenear.butterfly.global.application;

import com.codenear.butterfly.global.dto.DebeziumChangeEventDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.Configuration;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class DebeziumListener {
    private final DebeziumEngine<ChangeEvent<String, String>> debeziumEngine;
    private final ExecutorService executor;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public DebeziumListener(Configuration debeziumConnectorConfig, ObjectMapper objectMapper, ApplicationEventPublisher eventPublisher) {
        this.debeziumEngine = DebeziumEngine.create(Json.class)
                .using(debeziumConnectorConfig.asProperties())
                .notifying(this::handleChangeEvent)
                .build();
        this.objectMapper = objectMapper;
        this.executor = Executors.newSingleThreadExecutor();
        this.eventPublisher = eventPublisher;
    }

    /**
     * Debezium으로부터 변경 이벤트를 받아 처리
     *
     * @param changeEvent Debezium에서 발생한 변경 이벤트 (문자열 형태의 JSON 값 포함)
     */
    private void handleChangeEvent(ChangeEvent<String, String> changeEvent) {
        try {
            JsonNode payloadNode = validateAndGetPayload(changeEvent);
            if (payloadNode == null) {
                return;
            }

            // op필드 = DML (INSERT, UPDATE, DELETE) = 'c','u','d'
            JsonNode operationNode = payloadNode.get("op");

            // op필드에 대한 변경이 일어나면, 즉 INSERT,UPDATE,DELETE의 로직이 수행되면 (DDL 제외)
            if (operationNode != null) {
                handleDmlEvent(operationNode.asText(), payloadNode, changeEvent.destination(), changeEvent.value());
            }
        } catch (Exception e) {
            log.error("변경 이벤트 처리 중 오류 발생. 값: {}", changeEvent.value(), e);
        }
    }

    /**
     * Debezium 이벤트의 유효성을 검사하고 페이로드 JsonNode를 반환
     * 이벤트 값이나 페이로드가 유효하지 않으면 null을 반환 및 로그 발행
     *
     * @param changeEvent Debezium 변경 이벤트
     * @return 유효한 경우 페이로드 JsonNode, 그렇지 않으면 null
     * @throws IOException JSON 파싱 중 오류 발생 시
     */
    private JsonNode validateAndGetPayload(ChangeEvent<String, String> changeEvent) throws IOException {
        String value = changeEvent.value();
        if (value == null) {
            log.warn("값이 null인 변경 이벤트를 수신했습니다. 이벤트를 건너뜜니다.");
            return null;
        }

        JsonNode jsonNode = objectMapper.readTree(value);
        JsonNode payloadNode = jsonNode.get("payload");

        if (payloadNode == null) {
            log.warn("페이로드가 null인 변경 이벤트를 수신했습니다: {}", value);
            return null;
        }
        return payloadNode;
    }

    /**
     * DML(INSERT, UPDATE, DELETE, READ) 이벤트를 처리하는 메서드.
     */
    private void handleDmlEvent(String operation, JsonNode payloadNode, String topic, String rawValue) {
        String table = topic.substring(topic.lastIndexOf('.') + 1);

        JsonNode dataPayload;

        // before : 변경 또는 삭제 되기 전의 데이터 / after : 변경 또는 삭제 된 후의 데이터
        dataPayload = operation.equals("d") ? payloadNode.get("before") : payloadNode.get("after");

        if (dataPayload == null) {
            log.warn("작업 '{}' 테이블 '{}'에 대한 데이터 페이로드(before/after)가 null입니다. 이벤트 값: {}", operation, table, rawValue);
            return;
        }

        log.debug("DML 변경 감지됨: 작업={}, 테이블={}, 데이터={}", operation, table, dataPayload);
        eventPublisher.publishEvent(new DebeziumChangeEventDTO(this, operation, table, dataPayload, payloadNode));
    }

    /**
     * 스프링 컴포넌트 초기화 시 Debezium 엔진을 시작
     */
    @PostConstruct
    public void start() {
        executor.execute(debeziumEngine);
    }

    /**
     * 스프링 컴포넌트 종료 시 Debezium 엔진을 중지하고 리소스를 해제합니다.
     */
    @PreDestroy
    public void stop() {
        if (debeziumEngine != null) {
            try {
                debeziumEngine.close();
            } catch (IOException e) {
                log.error("Error while stopping Debezium engine", e);
            }
        }
        executor.shutdown();
    }
}