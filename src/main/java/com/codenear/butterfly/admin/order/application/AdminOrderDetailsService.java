package com.codenear.butterfly.admin.order.application;

import com.codenear.butterfly.admin.order.exception.OrderException;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.dto.OrderStatus;
import com.codenear.butterfly.payment.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.Price;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codenear.butterfly.notify.NotifyMessage.PRODUCT_ARRIVAL;
import static com.codenear.butterfly.notify.NotifyMessage.REWARD_POINT;

@Service
@RequiredArgsConstructor
public class AdminOrderDetailsService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final PointRepository pointRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final FCMFacade fcmFacade;

    public Page<OrderDetails> getAllOrders(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return orderDetailsRepository.findAll(pageable);
    }

    public Page<OrderDetails> getOrdersByStatus(OrderStatus status, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return orderDetailsRepository.findByOrderStatus(status, pageable);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        OrderDetails order = orderDetailsRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND, ErrorCode.ORDER_NOT_FOUND.getMessage()));

        order.updateOrderStatus(newStatus);
        orderDetailsRepository.save(order);

        if (newStatus.equals(OrderStatus.COMPLETED)) {
            ProductInventory product = productInventoryRepository.findProductByProductName(order.getProductName());
            increaseRefundPoint(product, order);
            fcmFacade.sendMessage(PRODUCT_ARRIVAL, order.getMember().getId());
        }
    }

    /**
     * 포인트 백 가격 만큼 사용자 포인트 증가
     *
     * @param product 상품
     * @param order   구매 내역
     */
    private void increaseRefundPoint(ProductInventory product, OrderDetails order) {
        Point point = pointRepository.findByMember(order.getMember())
                .orElseGet(() -> {
                    Point newPoint = Point.createPoint()
                            .member(order.getMember())
                            .build();
                    return pointRepository.save(newPoint);
                });

        int refundPoint = calculateRefundPoint(product, order);
        if (refundPoint > 0) {
            point.increasePoint(refundPoint);
        }
        sendRewordMessage(refundPoint, order.getMember().getId());
    }

    /**
     * 최종 포인트백 가격 계산
     *
     * @param product 상품
     * @param order   구매 내역
     * @return 제공할 최종 포인트
     */
    private int calculateRefundPoint(ProductInventory product, OrderDetails order) {
        // 현재 할인가
        int currentDiscountPrice = currentDiscountPrice(product);
        // 구매당시 할인가
        int memberPurchasedPrice = getPurchasePriceOfPiece(order);
        int refundPoint = memberPurchasedPrice - currentDiscountPrice;

        // 현재 할인가격이 구매당시 할인가격보다 저렴할 때 (차액 * 구매개수) 만큼 포인트백, 만약 현재 할인가격이 더 비싸다면 0
        return refundPoint > 0 ? refundPoint * order.getQuantity() : 0;
    }

    /**
     * 상품의 현재 할인가격 가져오기
     *
     * @param product 상품
     * @return 현재 할인가
     */
    private int currentDiscountPrice(ProductInventory product) {
        Price price = Price.of(
                product.getOriginalPrice(),
                product.getSaleRate(),
                product.getCurrentDiscountRate()
        );

        return price.calculateSalePrice();
    }

    /**
     * 사용자가 구매한 상품의 1개당 구매 가격
     *
     * @param order 구매 내역
     * @return 개당 구매 가격
     */
    private int getPurchasePriceOfPiece(OrderDetails order) {
        return order.getTotal() / order.getQuantity();
    }

    /**
     * 환급 포인트가 0원 이상이라면 포인트백 지급 알림을 보낸다.
     *
     * @param reward   환급액
     * @param memberId 사용자 아이디
     */
    private void sendRewordMessage(int reward, Long memberId) {
        if (reward > 0) {
            fcmFacade.sendMessage(REWARD_POINT, memberId);
        }
    }

    /**
     * 일괄 주문 상태 변경
     *
     * @param orderIds 변경할 주문 아이디
     * @param status   변경 상태
     * @return 변경된 주문 개수
     */
    @Transactional
    public int bulkChangeOrderStatus(List<Long> orderIds, OrderStatus status) {
        int updatedCnt = orderDetailsRepository.updateOrderStatusInBulk(
                orderIds, status);

        if (OrderStatus.COMPLETED.equals(status) && updatedCnt > 0) {
            List<OrderDetails> updatedOrders = new ArrayList<>(orderDetailsRepository.findAllById(orderIds));
            processPointsAndNotificationsBatch(updatedOrders);
        }

        return updatedCnt;
    }

    private void processPointsAndNotificationsBatch(List<OrderDetails> orders) {
        // 관련 상품 정보 한 번에 조회
        Set<String> productNames = new HashSet<>();
        for (OrderDetails order : orders) {
            productNames.add(order.getProductName());
        }

        List<ProductInventory> products = productInventoryRepository.findAllByProductNameIn(productNames);
        Map<String, ProductInventory> productMap = new HashMap<>();
        for (ProductInventory product : products) {
            productMap.put(product.getProductName(), product);
        }

        // 회원별 포인트 그룹화
        Map<Long, Integer> memberPointMap = new HashMap<>();
        Set<Long> membersToNotify = new HashSet<>();

        // 주문별 포인트 계산
        for (OrderDetails order : orders) {
            ProductInventory product = productMap.get(order.getProductName());
            if (product != null) {
                int refundPoint = calculateRefundPoint(product, order);

                // 회원별 포인트 누적
                Long memberId = order.getMember().getId();
                memberPointMap.merge(memberId, refundPoint, Integer::sum); //cf.containsKey

                // 알림 대상 회원 추가
                membersToNotify.add(memberId);
            }
        }

        // 회원별로 포인트 한 번에 업데이트
        // cf.keySet,get
        for (Map.Entry<Long, Integer> entry : memberPointMap.entrySet()) {
            Long memberId = entry.getKey();
            Integer totalPoints = entry.getValue();

            if (totalPoints > 0) {
                pointRepository.increasePointByMemberId(memberId, totalPoints);
            }
        }

        // 한 번에 알림 발송
        for (Long memberId : membersToNotify) {
            fcmFacade.sendMessage(PRODUCT_ARRIVAL, memberId);
            // getOrDefault : 키 존재하지 않을 때 기본값 반환
            if (memberPointMap.getOrDefault(memberId, 0) > 0) {
                fcmFacade.sendMessage(REWARD_POINT, memberId);
            }
        }
    }
}
