package com.codenear.butterfly.payment.kakaoPay.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReadyResponseDTO {

    private String tid; // 결제 고유 번호
    private String next_redirect_app_url; // 결제 페이지 url 받기
    private String next_redirect_mobile_url; // 모바일 다이렉트 url
    private String next_redirect_pc_url; // 로컬 테스트용 url
    private String android_app_scheme;
    private String ios_app_scheme;
    private String created_at;
}
