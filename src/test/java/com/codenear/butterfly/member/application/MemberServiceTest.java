package com.codenear.butterfly.member.application;

import com.codenear.butterfly.certify.application.CertifyService;
import com.codenear.butterfly.certify.domain.CertifyType;
import com.codenear.butterfly.certify.domain.dto.CertifyRequest;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.repository.member.MemberRepository;
import com.codenear.butterfly.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CertifyService certifyService;

    @InjectMocks
    private MemberService memberService;

    @DisplayName("아이디찾기-핸드폰인증-없는유저")
    @Test
    void findUserByPhoneNumber_NotFound() {
        String phoneNumber = "01012345678";
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThrows(MemberException.class,
                () -> memberService.findUserByPhoneNumber(phoneNumber),
                "등록되지 않은 회원입니다."
        );
    }

    @Test
    @DisplayName("아이디찾기-핸드폰인증-성공")
    void findUserByPhoneNumberSuccess() {
        String phoneNumber = "010-1234-5678";
        Member mockMember = Member.builder()
                .id(1L)
                .phoneNumber(phoneNumber)
                .email("test@example.com")
                .build();

        when(memberRepository.findByPhoneNumber(phoneNumber))
                .thenReturn(Optional.of(mockMember));

        memberService.findUserByPhoneNumber(phoneNumber);

        verify(certifyService).sendCertifyCode(phoneNumber, CertifyType.REGISTER_PHONE);
    }

    @DisplayName("아이디찾기-성공(이메일반환)")
    @Test
    void findEmail_success() {
        String phoneNumber = "01012345678";
        String email = "test@example.com";

        Member mockMember = Mockito.mock(Member.class);

        CertifyRequest certifyRequest = new CertifyRequest(phoneNumber, "123456");
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(mockMember));
        when(mockMember.getEmail()).thenReturn(email);

        doNothing().when(certifyService).checkCertifyCode(certifyRequest, CertifyType.REGISTER_PHONE);

        String returnEmail = memberService.findEmail(certifyRequest);

        assertThat(returnEmail).isEqualTo(email);

        verify(certifyService).checkCertifyCode(certifyRequest, CertifyType.REGISTER_PHONE);
        verify(memberRepository).findByPhoneNumber(phoneNumber);
    }

    @DisplayName("아이디찾기-없는유저")
    @Test
    void findEmail_NotFound() {
        String phoneNumber = "01012345678";
        CertifyRequest certifyRequest = new CertifyRequest(phoneNumber, "123456");

        doNothing().when(certifyService).checkCertifyCode(certifyRequest, CertifyType.REGISTER_PHONE);

        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThrows(MemberException.class,
                () -> memberService.findEmail(certifyRequest),
                "등록되지 않은 회원입니다."
        );

        verify(certifyService).checkCertifyCode(certifyRequest, CertifyType.REGISTER_PHONE);
        verify(memberRepository).findByPhoneNumber(phoneNumber);
    }
}