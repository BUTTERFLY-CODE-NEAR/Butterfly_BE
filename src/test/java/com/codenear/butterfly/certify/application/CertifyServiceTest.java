package com.codenear.butterfly.certify.application;

import static com.codenear.butterfly.certify.domain.CertifyType.CERTIFY_PHONE;
import static com.codenear.butterfly.global.exception.ErrorCode.CERTIFY_CODE_EXPIRED;
import static com.codenear.butterfly.global.exception.ErrorCode.CERTIFY_CODE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codenear.butterfly.certify.domain.CertifyType;
import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.certify.exception.CertifyException;
import com.codenear.butterfly.sms.application.SmsService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class CertifyServiceTest {

    @Mock
    private SmsService smsService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CertifyService certifyService;

    private final String phoneNumber = "01012345678";
    private final String code = "123456";
    private final CertifyType type = CERTIFY_PHONE;
    private final CertifyRequest request = new CertifyRequest(phoneNumber, null, code);

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void 인증번호_전송_및_저장을_성공한다() {
        // when
        certifyService.sendCertifyCode(phoneNumber, type);

        // then
        verify(smsService).sendSMS(eq(phoneNumber), anyString());
        verify(redisTemplate.opsForValue()).set(anyString(), anyString(), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    void 유효한_인증번호_검증을_성공한다() {
        // given
        when(valueOperations.get(anyString())).thenReturn(code);

        // when
        certifyService.checkCertifyCode(request, type);

        // then
        verify(redisTemplate).delete(anyString());
    }

    @Test
    void 잘못된_인증번호는_예외가_발생한다() {
        // given
        when(valueOperations.get(anyString())).thenReturn("654321");

        // when & then
        assertThatThrownBy(() -> certifyService.checkCertifyCode(request, type))
                .isInstanceOf(CertifyException.class)
                .hasMessage(CERTIFY_CODE_MISMATCH.getMessage());
    }

    @Test
    void 만료된_인증번호는_예외가_발생한다() {
        // given
        when(valueOperations.get(anyString())).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> certifyService.checkCertifyCode(request, type))
                .isInstanceOf(CertifyException.class)
                .hasMessage(CERTIFY_CODE_EXPIRED.getMessage());
    }
}