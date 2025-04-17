package com.codenear.butterfly.admin.order.application;

import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import com.codenear.butterfly.kakaoPay.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminOrderDetailsServiceTest {

    @InjectMocks
    private AdminOrderDetailsService orderService;

    @Mock
    private OrderDetailsRepository orderDetailsRepository;

    @Mock
    private ProductInventoryRepository productInventoryRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private FCMFacade fcmFacade;

    @Captor
    private ArgumentCaptor<Long> memberIdCaptor;

    private Member member1, member2;
    private OrderDetails order1, order2, order3;
    private ProductInventory product1, product2;

    @BeforeEach
    void setUp() {
        // Member 객체 생성
        member1 = Member.builder()
                .id(1L)
                .username("user1")
                .email("user1@test.com")
                .nickname("nickname1")
                .build();

        member2 = Member.builder()
                .id(2L)
                .username("user2")
                .email("user2@test.com")
                .nickname("nickname2")
                .build();

        // 테스트용 Product 및 ProductInventory 객체 생성
        product1 = mock(ProductInventory.class);
        when(product1.getProductName()).thenReturn("상품A");
        when(product1.getOriginalPrice()).thenReturn(10000);
        when(product1.getSaleRate()).thenReturn(BigDecimal.ZERO); // 기본 할인율 0%
        when(product1.getCurrentDiscountRate()).thenReturn(BigDecimal.valueOf(20)); // 참여 할인율 20%

        product2 = mock(ProductInventory.class);
        when(product2.getProductName()).thenReturn("상품B");
        when(product2.getOriginalPrice()).thenReturn(20000);
        when(product2.getSaleRate()).thenReturn(BigDecimal.ZERO); // 기본 할인율 0%
        when(product2.getCurrentDiscountRate()).thenReturn(BigDecimal.valueOf(10)); // 참여 할인율 10%

        // OrderDetails 객체 생성
        order1 = createOrderDetails(101L, member1, "상품A", OrderStatus.DELIVERY, 1, 10000);
        order2 = createOrderDetails(102L, member1, "상품B", OrderStatus.DELIVERY, 1, 20000);
        order3 = createOrderDetails(103L, member2, "상품A", OrderStatus.DELIVERY, 1, 10000);
    }

    // 테스트용 OrderDetails 객체 생성 메서드
    private OrderDetails createOrderDetails(Long id, Member member, String productName, OrderStatus status, int quantity, int total) {
        OrderDetails order = mock(OrderDetails.class);
        when(order.getId()).thenReturn(id);
        when(order.getMember()).thenReturn(member);
        when(order.getProductName()).thenReturn(productName);
        when(order.getOrderStatus()).thenReturn(status);
        when(order.getQuantity()).thenReturn(quantity);
        when(order.getTotal()).thenReturn(total);

        return order;
    }

    @Test
    public void 일괄배송_성공() {
        // Given
        List<Long> orderIds = Arrays.asList(101L, 102L, 103L);

        // 상태 업데이트 성공 (3건)
        when(orderDetailsRepository.updateOrderStatusInBulk(
                orderIds, OrderStatus.DELIVERY, OrderStatus.COMPLETED))
                .thenReturn(3);

        // 업데이트된 주문들의 상태가 COMPLETED로 변경됨
        OrderDetails completedOrder1 = createOrderDetails(101L, member1, "상품A", OrderStatus.COMPLETED, 1, 10000);
        OrderDetails completedOrder2 = createOrderDetails(102L, member1, "상품B", OrderStatus.COMPLETED, 1, 20000);
        OrderDetails completedOrder3 = createOrderDetails(103L, member2, "상품A", OrderStatus.COMPLETED, 1, 10000);

        when(orderDetailsRepository.findAllById(orderIds))
                .thenReturn(Arrays.asList(completedOrder1, completedOrder2, completedOrder3));

        // 상품 정보 조회
        Set<String> productNames = new HashSet<>(Arrays.asList("상품A", "상품B"));
        when(productInventoryRepository.findAllByProductNameIn(productNames))
                .thenReturn(Arrays.asList(product1, product2));

        // When
        int result = orderService.bulkCompleteOrders(orderIds);

        // Then
        assertThat(result).isEqualTo(3);

        // 포인트 업데이트 검증 - 실제 계산 결과 반영
        // 상품A: 10000 - 8000 = 2000 (20% 할인)
        // 상품B: 20000 - 18000 = 2000 (10% 할인)
        verify(pointRepository).increasePointByMemberId(eq(1L), eq(4000)); // member1: 2000 (상품A) + 2000 (상품B)
        verify(pointRepository).increasePointByMemberId(eq(2L), eq(2000)); // member2: 2000 (상품A)

        // 알림 발송 검증
        verify(fcmFacade, times(2)).sendMessage(eq(NotifyMessage.PRODUCT_ARRIVAL), memberIdCaptor.capture());
        verify(fcmFacade, times(2)).sendMessage(eq(NotifyMessage.REWARD_POINT), memberIdCaptor.capture());

        List<Long> capturedMemberIds = memberIdCaptor.getAllValues();
        assertThat(capturedMemberIds).contains(1L, 2L);
    }

    @Test
    public void 일괄배송_상태변경_실패() {
        // Given
        List<Long> orderIds = Arrays.asList(101L, 102L, 103L);

        // 상태 업데이트 실패 (0건)
        when(orderDetailsRepository.updateOrderStatusInBulk(
                orderIds, OrderStatus.DELIVERY, OrderStatus.COMPLETED))
                .thenReturn(0);

        // When
        int result = orderService.bulkCompleteOrders(orderIds);

        // Then
        assertThat(result).isEqualTo(0);

        // processPointsAndNotificationsBatch 메서드가 호출되지 않았는지 검증
        verify(productInventoryRepository, never()).findAllByProductNameIn(any());
        verify(pointRepository, never()).increasePointByMemberId(anyLong(), anyInt());
        verify(fcmFacade, never()).sendMessage(any(NotifyMessage.class), anyLong());
    }

    @Test
    public void 일괄배송_일부_성공() {
        // Given
        List<Long> orderIds = Arrays.asList(101L, 102L, 103L);

        // 상태 업데이트 부분 성공 (2건)
        when(orderDetailsRepository.updateOrderStatusInBulk(
                orderIds, OrderStatus.DELIVERY, OrderStatus.COMPLETED))
                .thenReturn(2);

        // 첫 번째와 세 번째 주문만 COMPLETED로 변경됨
        OrderDetails completedOrder1 = createOrderDetails(101L, member1, "상품A", OrderStatus.COMPLETED, 1, 10000);
        OrderDetails nonCompletedOrder2 = createOrderDetails(102L, member1, "상품B", OrderStatus.DELIVERY, 1, 20000); // 이 주문은 업데이트 안됨
        OrderDetails completedOrder3 = createOrderDetails(103L, member2, "상품A", OrderStatus.COMPLETED, 1, 10000);

        when(orderDetailsRepository.findAllById(orderIds))
                .thenReturn(Arrays.asList(completedOrder1, nonCompletedOrder2, completedOrder3));

        // 상품 정보 조회 - 상품A만 필요
        Set<String> productNames = new HashSet<>(Arrays.asList("상품A"));
        when(productInventoryRepository.findAllByProductNameIn(productNames))
                .thenReturn(Arrays.asList(product1));

        // When
        int result = orderService.bulkCompleteOrders(orderIds);

        // Then
        assertThat(result).isEqualTo(2);

        // filter 메서드에 의해 COMPLETED 상태인 주문들만 처리되는지 검증
        verify(pointRepository).increasePointByMemberId(eq(1L), eq(2000)); // member1: 2000 (상품A)
        verify(pointRepository).increasePointByMemberId(eq(2L), eq(2000)); // member2: 2000 (상품A)

        // 알림 발송 검증
        verify(fcmFacade, times(2)).sendMessage(eq(NotifyMessage.PRODUCT_ARRIVAL), anyLong());
        verify(fcmFacade, times(2)).sendMessage(eq(NotifyMessage.REWARD_POINT), anyLong());
    }

    @Test
    public void 배송완료_포인트지급_및_알림발송() {
        // Given
        Long orderId = 101L;
        OrderDetails order = createOrderDetails(orderId, member1, "상품A", OrderStatus.DELIVERY, 1, 10000);
        when(orderDetailsRepository.findById(orderId)).thenReturn(Optional.of(order));

        // 상품 정보 조회
        when(productInventoryRepository.findProductByProductName("상품A")).thenReturn(product1);

        // 회원 포인트
        Point memberPoint = mock(Point.class);
        when(pointRepository.findByMember(member1)).thenReturn(Optional.of(memberPoint));

        // When
        orderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);

        // Then
        verify(order).updateOrderStatus(OrderStatus.COMPLETED);
        verify(orderDetailsRepository).save(order);

        // 포인트 증가 검증 - 실제 Price 계산 결과 (10000 - 8000 = 2000)
        verify(memberPoint).increasePoint(2000);

        // 알림 발송 검증
        verify(fcmFacade).sendMessage(NotifyMessage.PRODUCT_ARRIVAL, member1.getId());
        verify(fcmFacade).sendMessage(NotifyMessage.REWARD_POINT, member1.getId());
    }

    @Test
    public void 배송완료_포인트없는경우_알림만발송() {
        // Given
        Long orderId = 101L;
        OrderDetails order = createOrderDetails(orderId, member1, "상품A", OrderStatus.DELIVERY, 1, 10000);
        when(orderDetailsRepository.findById(orderId)).thenReturn(Optional.of(order));

        // 상품 정보 조회 - 현재가가 구매가와 동일하도록 설정
        ProductInventory samePrice = mock(ProductInventory.class);
        when(samePrice.getProductName()).thenReturn("상품A");
        when(samePrice.getOriginalPrice()).thenReturn(10000);
        when(samePrice.getSaleRate()).thenReturn(BigDecimal.ZERO);
        when(samePrice.getCurrentDiscountRate()).thenReturn(BigDecimal.ZERO); // 할인 없음

        when(productInventoryRepository.findProductByProductName("상품A")).thenReturn(samePrice);

        // 회원 포인트
        Point memberPoint = mock(Point.class);
        when(pointRepository.findByMember(member1)).thenReturn(Optional.of(memberPoint));

        // When
        orderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);

        // Then
        verify(order).updateOrderStatus(OrderStatus.COMPLETED);
        verify(orderDetailsRepository).save(order);

        verify(memberPoint, never()).increasePoint(anyInt());

        // 상품 도착 알림만 발송됨
        verify(fcmFacade).sendMessage(NotifyMessage.PRODUCT_ARRIVAL, member1.getId());
        verify(fcmFacade, never()).sendMessage(eq(NotifyMessage.REWARD_POINT), anyLong());
    }
}