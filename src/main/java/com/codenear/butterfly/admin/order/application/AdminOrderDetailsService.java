package com.codenear.butterfly.admin.order.application;

import com.codenear.butterfly.admin.order.exception.OrderException;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
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

import java.util.List;
import java.util.stream.Collectors;

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
        point.increasePoint(refundPoint);
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

    @Transactional
    public int bulkCompleteOrders(List<Long> orderIds) {
        List<OrderDetails> orders = orderDetailsRepository.findAllById(orderIds).stream()
                .filter(order -> OrderStatus.DELIVERY.equals(order.getOrderStatus()))
                .collect(Collectors.toList());

        for (OrderDetails order : orders) {
            order.updateOrderStatus(OrderStatus.COMPLETED);
            orderDetailsRepository.save(order);

            // 포인트백 처리 및 알림 발송
            ProductInventory product = productInventoryRepository.findProductByProductName(order.getProductName());
            increaseRefundPoint(product, order);
            fcmFacade.sendMessage(PRODUCT_ARRIVAL, order.getMember().getId());
        }

        return orders.size();
    }
}
