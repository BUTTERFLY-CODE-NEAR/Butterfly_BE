package com.codenear.butterfly.kakaoPay.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApproveRequestDTO {
    private String tid;
    private String orderId;
    private String memberId;
    private String pgToken;
}
