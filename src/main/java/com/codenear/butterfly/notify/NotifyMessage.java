package com.codenear.butterfly.notify;

import com.codenear.butterfly.consent.domain.ConsentType;
import com.codenear.butterfly.notify.exception.NotifyException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static com.codenear.butterfly.consent.domain.ConsentType.CUSTOMER_SUPPORT;
import static com.codenear.butterfly.consent.domain.ConsentType.DELIVERY_NOTIFICATION;
import static com.codenear.butterfly.consent.domain.ConsentType.MARKETING;
import static com.codenear.butterfly.consent.domain.ConsentType.POINT_BACK;
import static com.codenear.butterfly.global.exception.ErrorCode.NOTIFY_MESSAGE_NOT_FOUND;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotifyMessage {

    NEW_PRODUCT(1, "신상품 업로드", "\uD83C\uDF81 놓치지 마세요! 신규 상품 입고", "나비에 신상품이 추가됐어요. 지금 확인하고 가장 먼저 만나보세요 \uD83C\uDF1F", MARKETING),
    REWARD_POINT(2, "포인트 백", "\uD83D\uDCB0 포인트 적립 완료!", "추가 할인을 통해 포인트가 적립되었어요. 포인트는 다음 구매에 사용할 수 있으니 잊지 말고 활용하세요! \uD83D\uDE04", POINT_BACK),
    PRODUCT_DELIVERY_DEPARTURE(3, "배송 시작", "\uD83D\uDE9A 주문하신 상품이 출발했습니다!", "배송이 진행 중이며, 도착 시 다시 알려 드릴게요! \uD83D\uDE0A\n", DELIVERY_NOTIFICATION),
    PRODUCT_ARRIVAL(4, "배송 도착", "\uD83D\uDCE6 상품이 도착했습니다!", "고객님의 상품이 배송 완료되었어요. 만족스러운 구매가 되셨길 바랍니다! \uD83D\uDE0A", DELIVERY_NOTIFICATION),
    INQUIRY_ANSWERED(5, "QnA 답변", "\uD83D\uDCEC 답변이 도착했습니다!", "문의하신 내용에 답변이 도착했어요. 지금 바로 확인해 보세요. \uD83D\uDCE8", CUSTOMER_SUPPORT),
    CITATION_PROMOTION(6, "프로모션 적립", "\uD83C\uDF89 전화번호 인증 완료! ", "1,000 포인트가 지급되었어요! 지금 바로 사용해 보세요 \uD83E\uDD29", MARKETING),
    ORDER_CANCELED(7, "주문 취소", "\uD83D\uDCEC 주문하신 상품이 취소되었습니다.", "고객님의 주문이 취소되었어요. \uD83D\uDCEC", MARKETING),
    ORDER_SUCCESS(8, "결제 완료", "\uD83E\uDD73 결제가 완료되었습니다.", "주문해주셔서 감사합니다. 꼼꼼히 확인해서 준비해드리겠습니다! \uD83D\uDE0A", MARKETING),
    RESTOCK_PRODUCT(9, "상품 재입고", "\uD83C\uDF81 상품이 재입고 되었습니다.", "신청하신 상품이 재입고 되었어요. 지금 확인하고 가장 먼저 만나보세요 \uD83C\uDF1F", MARKETING);

    private final int id;
    private final String title;
    private final String subtitle;
    private final String content;
    private final ConsentType consentType;

    public static NotifyMessage findByCode(int code) {
        return Arrays.stream(values())
                .filter(notify -> notify.getId() == code)
                .findFirst()
                .orElseThrow(() -> new NotifyException(NOTIFY_MESSAGE_NOT_FOUND, code));
    }
}
