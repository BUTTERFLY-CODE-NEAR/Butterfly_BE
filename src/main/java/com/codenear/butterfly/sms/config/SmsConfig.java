package com.codenear.butterfly.sms.config;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfig {

    public static final String COOLSMS_API_URL = "https://api.coolsms.co.kr";

    @Value("${cool.sms.api.key}")
    private String apiKey;

    @Value("${cool.sms.api.secret}")
    private String apiSecret;

    @Bean
    public DefaultMessageService messageService() {
        return NurigoApp.INSTANCE.initialize(
                apiKey,
                apiSecret,
                COOLSMS_API_URL
        );
    }
}
