package com.codenear.butterfly.notify.domain;

import static com.codenear.butterfly.consent.domain.ConsentType.MARKETING;

import com.codenear.butterfly.consent.domain.ConsentType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotifyMessage {

    NEW_PRODUCT("\uD83C\uDF81 놓치지 마세요! 신규 상품 입고", "신상품 업로드", "나비에 신상품이 추가됐어요. 지금 확인하고 가장 먼저 만나보세요 \uD83C\uDF1F", MARKETING),
    REWARD_POINT("\uD83D\uDCB0 포인트 적립 완료!", "포인트 백", "추가 할인을 통해 포인트가 적립되었어요. 포인트는 다음 구매에 사용할 수 있으니 잊지 말고 활용하세요! \uD83D\uDE04", MARKETING),
    PRODUCT_DELIVERY_DEPARTURE("\uD83D\uDE9A 주문하신 상품이 출발했습니다!", "배송 시작", "배송이 진행 중이며, 도착 시 다시 알려 드릴게요! \uD83D\uDE0A\n", MARKETING),
    PRODUCT_ARRIVAL("\uD83D\uDCE6 상품이 도착했습니다!", "배송 도착", "고객님의 상품이 배송 완료되었어요. 만족스러운 구매가 되셨길 바랍니다! \uD83D\uDE0A", MARKETING),
    INQUIRY_ANSWERED("\uD83D\uDCEC 답변이 도착했습니다!", "QnA 답변", "문의하신 내용에 답변이 도착했어요. 지금 바로 확인해 보세요. \uD83D\uDCE8", MARKETING),
    CITATION_PROMOTION("\uD83C\uDF89 전화번호 인증 완료! ", "프로모션 적립", "1,000 포인트가 지급되었어요! 지금 바로 사용해 보세요 \uD83E\uDD29", MARKETING)
    ;

    private final String title;
    private final String subtitle;
    private final String content;
    private final ConsentType consentType;
}
