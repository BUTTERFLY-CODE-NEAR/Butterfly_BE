package com.codenear.butterfly.member.application;

import com.codenear.butterfly.certify.application.CertifyService;
import com.codenear.butterfly.certify.domain.CertifyType;
import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.FindPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.ResetPasswordRequestDTO;
import com.codenear.butterfly.member.domain.enums.VerificationType;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class CredentialServiceTests {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CertifyService certifyService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private CredentialService credentialService;

    @Test
    void 아이디찾기_핸드폰인증_없는유저() {
        String phoneNumber = "01012345678";
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThrows(MemberException.class,
                () -> credentialService.sendFindEmailCode(phoneNumber),
                "해당 번호로 가입된 회원이 없습니다."
        );
    }

    @Test
    void 아이디찾기_핸드폰인증_성공() {
        String phoneNumber = "01012345678";
        Member mockMember = Member.builder()
                .id(1L)
                .phoneNumber(phoneNumber)
                .email("test@example.com")
                .build();

        when(memberRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.of(mockMember));

        credentialService.sendFindEmailCode(phoneNumber);

        verify(certifyService).sendCertifyCode(phoneNumber, CertifyType.CERTIFY_PHONE);
    }

    @Test
    void 아이디찾기_성공() {
        String phoneNumber = "01012345678";
        String email = "test@example.com";

        Member mockMember = Mockito.mock(Member.class);

        CertifyRequest certifyRequest = new CertifyRequest(phoneNumber, null, "123456");
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(mockMember));
        when(mockMember.getEmail()).thenReturn(email);

        doNothing().when(certifyService).checkCertifyCode(certifyRequest, CertifyType.CERTIFY_PHONE);

        String returnEmail = credentialService.findEmail(certifyRequest);

        assertThat(returnEmail).isEqualTo(email);

        verify(certifyService).checkCertifyCode(certifyRequest, CertifyType.CERTIFY_PHONE);
        verify(memberRepository).findByPhoneNumber(phoneNumber);
    }

    @Test
    void 아이디찾기_없는유저() {
        String phoneNumber = "01012345678";
        CertifyRequest certifyRequest = new CertifyRequest(phoneNumber, null, "123456");

        doNothing().when(certifyService).checkCertifyCode(certifyRequest, CertifyType.CERTIFY_PHONE);

        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThrows(MemberException.class,
                () -> credentialService.findEmail(certifyRequest),
                "해당 번호로 가입된 회원이 없습니다."
        );

        verify(certifyService).checkCertifyCode(certifyRequest, CertifyType.CERTIFY_PHONE);
        verify(memberRepository).findByPhoneNumber(phoneNumber);
    }

    @Test
    void 비밀번호찾기_핸드폰인증_없는유저() {
        String phoneNumber = "01012345678";
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThrows(MemberException.class,
                () -> credentialService.sendFindPasswordCode(new FindPasswordRequestDTO(phoneNumber, VerificationType.PHONE)),
                "해당 번호로 가입된 회원이 없습니다."
        );
    }

    @Test
    void 비밀번호재설정_핸드폰_없는유저(){
        String phoneNumber = "01012345678";
        String newPassword = "1q2w3e,./";

        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThrows(MemberException.class,
                () -> credentialService.resetPassword(new ResetPasswordRequestDTO(phoneNumber, VerificationType.PHONE, newPassword)),
                "해당 번호로 가입된 회원이 없습니다."
        );
    }

    @Test
    void 비밀번호재설정_핸드폰_성공(){
        String phoneNumber = "01012345678";
        String newPassword = "1q2w3e,./";

        Member mockMember = Member.builder().id(1L).phoneNumber(phoneNumber).build();
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded_1q2w3e,./");

        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO(phoneNumber, VerificationType.PHONE, newPassword);
        credentialService.resetPassword(request);

        assertThat(mockMember.getPassword()).isEqualTo("encoded_1q2w3e,./");
        verify(memberRepository).save(mockMember);
    }
}