package com.codenear.butterfly.admin.member.presentation;

import com.codenear.butterfly.admin.member.application.AdminMemberService;
import com.codenear.butterfly.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/member")
    public String showAdminMember(Model model) {
        List<Member> members = adminMemberService.loadAllUsers();
        model.addAttribute("members", members);
        return "admin/member/member";
    }
}
