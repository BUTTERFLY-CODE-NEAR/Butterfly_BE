package com.codenear.butterfly.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebeziumConfig {
    @Value("${debezium.database-hostname}")
    private String databaseHostname;
    @Value("${debezium.database-user}")
    private String databaseUser;
    @Value("${debezium.database-password}")
    private String databasePassword;
    @Value("${debezium.database-port}")
    private String databasePort;
    @Value("${debezium.name}")
    private String debeziumConnectorName;
    @Value("${debezium.database-server-id}")
    private String databaseServerId;
    @Value("${debezium.database-server-name}")
    private String databaseServerName;
    @Value("${debezium.database-include-list}")
    private String databaseIncludeList;
    @Value("${debezium.table-include-list}")
    private String tableIncludeList;
    @Value("${debezium.offset-filename}")
    private String offsetFilename;
    @Value("${debezium.history-filename}")
    private String historyFilename;

    @Bean
    public io.debezium.config.Configuration debeziumConnectorConfig() {
        return io.debezium.config.Configuration.create()
                /* Debezium 엔진 설정 */
                .with("name", debeziumConnectorName) // 커넥터 이름
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector") // 사용할 커넥터 클래스
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore") // offset 저장 방식 (파일)
                .with("offset.storage.file.filename", offsetFilename) // offset 저장 파일 경로
                .with("offset.flush.interval.ms", "30000") // offset 파일 저장 주기 (ms)

                /* 데이터베이스 연결 정보 */
                .with("database.hostname", databaseHostname) // DB 호스트
                .with("database.port", databasePort) // DB 포트
                .with("database.user", databaseUser) // DB 사용자
                .with("database.password", databasePassword) // DB 비밀번호
                .with("database.server.name", databaseServerName) // DB 서버의 논리적 이름 (고유해야 함)
                .with("topic.prefix", databaseServerName) // topic.prefix
                .with("database.include.list", databaseIncludeList) // 모니터링할 데이터베이스(스키마) 이름
                .with("database.server.id", databaseServerId) // DB 서버 아이디
                .with("table.include.list", tableIncludeList) // 특정 테이블만 모니터링할 경우

                /* 스키마 변경 이력 관리 */
                .with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory")
                .with("schema.history.internal.file.filename", historyFilename)
                .build();
    }
}