package com.codenear.butterfly.auth.application;

import com.codenear.butterfly.auth.application.email.EmailLoginService;
import com.codenear.butterfly.auth.application.email.EmailRegisterService;
import com.codenear.butterfly.auth.domain.dto.AuthLoginDTO;
import com.codenear.butterfly.auth.domain.dto.AuthRegisterDTO;
import com.codenear.butterfly.auth.exception.AuthException;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.member.domain.DeletedMember;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.member.domain.dto.MemberDTO;
import com.codenear.butterfly.member.domain.repository.member.DeletedMemberRepository;
import com.codenear.butterfly.member.infrastructure.MemberDataAccess;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final EmailRegisterService emailRegisterService;
    private final EmailLoginService emailLoginService;
    private final JwtService jwtService;
    private final MemberDataAccess memberDataAccess;
    private final DeletedMemberRepository deletedMemberRepository;

    public void handleRegistration(AuthRegisterDTO requestDTO, HttpServletResponse response) {
        Member member = emailRegisterService.emailRegister(requestDTO);
        jwtService.processTokens(member.getId(), response);
    }

    public void handleLogin(AuthLoginDTO requestDTO, HttpServletResponse response) {
        Member member = emailLoginService.login(requestDTO, requestDTO.getPassword());
        if (member.isDeleted()){
            throw new AuthException(ErrorCode.WITHDRAWN_ID, null);
        }
        jwtService.processTokens(member.getId(), response);
    }

    public void handleWithdraw(MemberDTO loginMember, HttpServletResponse response) {
        loginMember.getPlatform();
        memberDataAccess.evictMemberCache(loginMember.getId());

        Member member = memberDataAccess.findByMemberId(loginMember.getId());

        member.withdraw();
        DeletedMember deletedMember = DeletedMember.createDeletedMember(member);
        deletedMemberRepository.save(deletedMember);
        memberDataAccess.save(member);

//        Set<Member> linkedAccounts = memberDataAccess.getLinkedAccounts(member);
//        for (Member linkedMember : linkedAccounts) {
//            linkedMember.withdraw();
//            DeletedMember deletedMember = DeletedMember.createDeletedMember(linkedMember);
//            deletedMemberRepository.save(deletedMember);
//            memberDataAccess.save(linkedMember);
//            memberDataAccess.evictMemberCache(linkedMember.getId());
//        }

        // 쿠키 제거
        Cookie refreshCookie = new Cookie("Refresh", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        // 헤더 정리
        response.setHeader("Authorization", null);
    }

}