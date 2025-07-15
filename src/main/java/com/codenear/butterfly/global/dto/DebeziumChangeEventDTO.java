package com.codenear.butterfly.global.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DebeziumChangeEventDTO extends ApplicationEvent {
    private final String operationType;
    private final String tableName;
    private final JsonNode dataPayload;
    private final JsonNode fullPayload;

    public DebeziumChangeEventDTO(Object source, String operationType, String tableName, JsonNode dataPayload, JsonNode fullPayload) {
        super(source);
        this.operationType = operationType;
        this.tableName = tableName;
        this.dataPayload = dataPayload;
        this.fullPayload = fullPayload;
    }
}
