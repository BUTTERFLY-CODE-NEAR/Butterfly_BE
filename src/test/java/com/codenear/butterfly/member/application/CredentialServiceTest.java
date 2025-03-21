package com.codenear.butterfly.member.application;

import com.codenear.butterfly.certify.application.CertifyService;
import com.codenear.butterfly.certify.domain.CertifyType;
import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.Platform;
import com.codenear.butterfly.member.domain.dto.FindPasswordRequestDTO;
import com.codenear.butterfly.member.domain.dto.ResetPasswordRequestDTO;
import com.codenear.butterfly.member.domain.enums.VerificationType;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
public class CredentialServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CertifyService certifyService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private CredentialService credentialService;

    private static String phoneNumber = "01012345678";
    private static String email = "test@example.com";
    private static String newPassword = "1q2w3e,./";
    private static final Platform platform = Platform.CODENEAR;

    @Nested
    class FindEmailTest{
        @Test
        void 아이디찾기_핸드폰인증_없는유저() {
            when(memberRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);

            assertThrows(MemberException.class,
                    () -> credentialService.sendFindEmailCode(phoneNumber),
                    "일치하는 회원 정보가 없습니다."
            );
        }

        @Test
        void 아이디찾기_핸드폰인증_성공() {
            when(memberRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

            Member mockMember = Member.builder()
                    .id(1L)
                    .phoneNumber(phoneNumber)
                    .email(email)
                    .platform(platform)
                    .build();

            credentialService.sendFindEmailCode(phoneNumber);

            verify(certifyService).sendCertifyCode(phoneNumber, CertifyType.CERTIFY_PHONE);
        }

        @Test
        void 아이디찾기_성공() {
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
            CertifyRequest certifyRequest = new CertifyRequest(phoneNumber, null, "123456");

            doNothing().when(certifyService).checkCertifyCode(certifyRequest, CertifyType.CERTIFY_PHONE);

            when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

            assertThrows(MemberException.class,
                    () -> credentialService.findEmail(certifyRequest),
                    "일치하는 회원 정보가 없습니다."
            );

            verify(certifyService).checkCertifyCode(certifyRequest, CertifyType.CERTIFY_PHONE);
            verify(memberRepository).findByPhoneNumber(phoneNumber);
        }

    }


    @Nested
    class FindPasswordTest{
        @Test
        void 비밀번호찾기_핸드폰인증_없는유저() {
            when(memberRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);

            assertThrows(MemberException.class,
                    () -> credentialService.sendFindPasswordCode(new FindPasswordRequestDTO(phoneNumber, VerificationType.PHONE)),
                    "일치하는 회원 정보가 없습니다."
            );
        }

        @Test
        void 비밀번호재설정_핸드폰_없는유저(){
            when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

            assertThrows(MemberException.class,
                    () -> credentialService.resetPassword(new ResetPasswordRequestDTO(phoneNumber, VerificationType.PHONE, newPassword)),
                    "일치하는 회원 정보가 없습니다."
            );
        }

        @Test
        void 비밀번호재설정_핸드폰_성공(){
            Member mockMember = Member.builder().id(1L).phoneNumber(phoneNumber).build();
            when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(mockMember));
            when(passwordEncoder.encode(newPassword)).thenReturn("encoded_1q2w3e,./");

            ResetPasswordRequestDTO request = new ResetPasswordRequestDTO(phoneNumber, VerificationType.PHONE, newPassword);
            credentialService.resetPassword(request);

            assertThat(mockMember.getPassword()).isEqualTo("encoded_1q2w3e,./");
            verify(memberRepository).save(mockMember);
        }

        @Test
        void 비밀번호_인증코드발송_없는유저(){
            when(memberRepository.existsByEmail(email)).thenReturn(false);

            assertThrows(MemberException.class,
                    () -> credentialService.sendFindPasswordCode(new FindPasswordRequestDTO(email, VerificationType.EMAIL)),
                    "일치하는 회원 정보가 없습니다."
            );
        }

        @Test
        void 비밀번호_인증코드발송_성공(){
            when(memberRepository.existsByEmail(email)).thenReturn(true);

            FindPasswordRequestDTO request = new FindPasswordRequestDTO(email, VerificationType.EMAIL);
            credentialService.sendFindPasswordCode(request);

            verify(certifyService).sendCertifyCode(email, CertifyType.CERTIFY_EMAIL);
        }

        @Test
        void 비밀번호_재설정_없는유저(){
            when(memberRepository.findByEmailAndPlatform(email, platform)).thenReturn(Optional.empty());

            ResetPasswordRequestDTO request = new ResetPasswordRequestDTO(
                    email,
                    VerificationType.EMAIL,
                    newPassword
            );

            assertThrows(MemberException.class,
                    () -> credentialService.resetPassword(request),
                    "일치하는 회원 정보가 없습니다."
            );
        }

        @Test
        void 비밀번호_재설정_성공(){
            Member mockMember = Member.builder()
                    .id(1L)
                    .email(email)
                    .platform(platform)
                    .build();

            when(memberRepository.findByEmailAndPlatform(email, platform))
                    .thenReturn(Optional.of(mockMember));
            when(passwordEncoder.encode(newPassword))
                    .thenReturn("encoded_1q2w3e,./");

            ResetPasswordRequestDTO request = new ResetPasswordRequestDTO(
                    email,
                    VerificationType.EMAIL,
                    newPassword
            );

            credentialService.resetPassword(request);

            assertThat(mockMember.getPassword()).isEqualTo("encoded_1q2w3e,./");
            verify(memberRepository).save(mockMember);
        }
    }

}