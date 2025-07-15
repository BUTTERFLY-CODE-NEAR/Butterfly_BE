package com.codenear.butterfly.admin.order.application;

import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.notify.NotifyMessage;
import com.codenear.butterfly.notify.fcm.application.FCMFacade;
import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.dto.OrderStatus;
import com.codenear.butterfly.payment.domain.repository.OrderDetailsRepository;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.point.domain.PointRepository;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.codenear.butterfly.product.domain.repository.ProductInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(product1.getSaleRate()).thenReturn(BigDecimal.ZERO);
        when(product1.getCurrentDiscountRate()).thenReturn(BigDecimal.valueOf(20));

        product2 = mock(ProductInventory.class);
        when(product2.getProductName()).thenReturn("상품B");
        when(product2.getOriginalPrice()).thenReturn(20000);
        when(product2.getSaleRate()).thenReturn(BigDecimal.ZERO);
        when(product2.getCurrentDiscountRate()).thenReturn(BigDecimal.valueOf(10));

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

    // DELIVERY → COMPLETED
    @Test
    public void 일괄배송_성공_DELIVERY_to_COMPLETED() {
        // Given
        List<Long> orderIds = Arrays.asList(101L, 102L, 103L);

        when(orderDetailsRepository.updateOrderStatusInBulk(orderIds, OrderStatus.COMPLETED))
                .thenReturn(3);

        OrderDetails completedOrder1 = createOrderDetails(101L, member1, "상품A", OrderStatus.COMPLETED, 1, 10000);
        OrderDetails completedOrder2 = createOrderDetails(102L, member1, "상품B", OrderStatus.COMPLETED, 1, 20000);
        OrderDetails completedOrder3 = createOrderDetails(103L, member2, "상품A", OrderStatus.COMPLETED, 1, 10000);

        when(orderDetailsRepository.findAllById(orderIds))
                .thenReturn(Arrays.asList(completedOrder1, completedOrder2, completedOrder3));

        Set<String> productNames = new HashSet<>(Arrays.asList("상품A", "상품B"));
        when(productInventoryRepository.findAllByProductNameIn(productNames))
                .thenReturn(Arrays.asList(product1, product2));

        // When
        int result = orderService.bulkChangeOrderStatus(orderIds, OrderStatus.COMPLETED);

        // Then
        assertThat(result).isEqualTo(3);
        verify(pointRepository).increasePointByMemberId(eq(1L), eq(4000)); // member1: 2000 (상품A) + 2000 (상품B)
        verify(pointRepository).increasePointByMemberId(eq(2L), eq(2000)); // member2: 2000 (상품A)
        verify(fcmFacade, times(2)).sendMessage(eq(NotifyMessage.PRODUCT_ARRIVAL), memberIdCaptor.capture());
        verify(fcmFacade, times(2)).sendMessage(eq(NotifyMessage.REWARD_POINT), memberIdCaptor.capture());

        List<Long> capturedMemberIds = memberIdCaptor.getAllValues();
        assertThat(capturedMemberIds).contains(1L, 2L);
    }

    // READY → DELIVERY
    @Test
    public void 일괄배송_성공_READY_to_DELIVERY() {
        // Given
        List<Long> orderIds = Arrays.asList(101L, 102L, 103L);

        // READY 상태로 주문 설정
        order1 = createOrderDetails(101L, member1, "상품A", OrderStatus.READY, 1, 10000);
        order2 = createOrderDetails(102L, member1, "상품B", OrderStatus.READY, 1, 20000);
        order3 = createOrderDetails(103L, member2, "상품A", OrderStatus.READY, 1, 10000);

        when(orderDetailsRepository.updateOrderStatusInBulk(orderIds, OrderStatus.DELIVERY))
                .thenReturn(3);

        OrderDetails deliveryOrder1 = createOrderDetails(101L, member1, "상품A", OrderStatus.DELIVERY, 1, 10000);
        OrderDetails deliveryOrder2 = createOrderDetails(102L, member1, "상품B", OrderStatus.DELIVERY, 1, 20000);
        OrderDetails deliveryOrder3 = createOrderDetails(103L, member2, "상품A", OrderStatus.DELIVERY, 1, 10000);

        when(orderDetailsRepository.findAllById(orderIds))
                .thenReturn(Arrays.asList(deliveryOrder1, deliveryOrder2, deliveryOrder3));

        // When
        int result = orderService.bulkChangeOrderStatus(orderIds, OrderStatus.DELIVERY);

        // Then
        assertThat(result).isEqualTo(3);
        verify(pointRepository, never()).increasePointByMemberId(anyLong(), anyInt());
        verify(fcmFacade, never()).sendMessage(any(NotifyMessage.class), anyLong());
    }

    // 상태 변경 실패
    @Test
    public void 일괄배송_상태변경_실패() {
        // Given
        List<Long> orderIds = Arrays.asList(101L, 102L, 103L);

        when(orderDetailsRepository.updateOrderStatusInBulk(orderIds, OrderStatus.COMPLETED))
                .thenReturn(0);

        // When
        int result = orderService.bulkChangeOrderStatus(orderIds, OrderStatus.COMPLETED);

        // Then
        assertThat(result).isEqualTo(0);
        verify(productInventoryRepository, never()).findAllByProductNameIn(any());
        verify(pointRepository, never()).increasePointByMemberId(anyLong(), anyInt());
        verify(fcmFacade, never()).sendMessage(any(NotifyMessage.class), anyLong());
    }

    // 일부 성공 (DELIVERY → COMPLETED)
    @Test
    public void 일괄배송_일부_성공_DELIVERY_to_COMPLETED() {
        // Given
        List<Long> orderIds = Arrays.asList(101L, 102L, 103L);

        when(orderDetailsRepository.updateOrderStatusInBulk(orderIds, OrderStatus.COMPLETED))
                .thenReturn(2);

        OrderDetails completedOrder1 = createOrderDetails(101L, member1, "상품A", OrderStatus.COMPLETED, 1, 10000);
        OrderDetails nonCompletedOrder2 = createOrderDetails(102L, member1, "상품B", OrderStatus.DELIVERY, 1, 20000);
        OrderDetails completedOrder3 = createOrderDetails(103L, member2, "상품A", OrderStatus.COMPLETED, 1, 10000);

        when(orderDetailsRepository.findAllById(orderIds))
                .thenReturn(Arrays.asList(completedOrder1, nonCompletedOrder2, completedOrder3));

        Set<String> productNames = new HashSet<>(Arrays.asList("상품A", "상품B"));
        when(productInventoryRepository.findAllByProductNameIn(productNames))
                .thenReturn(Arrays.asList(product1));

        // When
        int result = orderService.bulkChangeOrderStatus(orderIds, OrderStatus.COMPLETED);

        // Then
        assertThat(result).isEqualTo(2);
        verify(pointRepository).increasePointByMemberId(eq(1L), eq(2000));
        verify(pointRepository).increasePointByMemberId(eq(2L), eq(2000));
        verify(fcmFacade, times(2)).sendMessage(eq(NotifyMessage.PRODUCT_ARRIVAL), anyLong());
        verify(fcmFacade, times(2)).sendMessage(eq(NotifyMessage.REWARD_POINT), anyLong());
    }

    // 일부 성공 (READY → DELIVERY)
    @Test
    public void 일괄배송_일부_성공_READY_to_DELIVERY() {
        // Given
        List<Long> orderIds = Arrays.asList(101L, 102L, 103L);

        order1 = createOrderDetails(101L, member1, "상품A", OrderStatus.READY, 1, 10000);
        order2 = createOrderDetails(102L, member1, "상품B", OrderStatus.READY, 1, 20000);
        order3 = createOrderDetails(103L, member2, "상품A", OrderStatus.READY, 1, 10000);

        when(orderDetailsRepository.updateOrderStatusInBulk(orderIds, OrderStatus.DELIVERY))
                .thenReturn(2);

        OrderDetails deliveryOrder1 = createOrderDetails(101L, member1, "상품A", OrderStatus.DELIVERY, 1, 10000);
        OrderDetails nonDeliveryOrder2 = createOrderDetails(102L, member1, "상품B", OrderStatus.READY, 1, 20000);
        OrderDetails deliveryOrder3 = createOrderDetails(103L, member2, "상품A", OrderStatus.DELIVERY, 1, 10000);

        when(orderDetailsRepository.findAllById(orderIds))
                .thenReturn(Arrays.asList(deliveryOrder1, nonDeliveryOrder2, deliveryOrder3));

        // When
        int result = orderService.bulkChangeOrderStatus(orderIds, OrderStatus.DELIVERY);

        // Then
        assertThat(result).isEqualTo(2);
    }

    // DELIVERY → COMPLETED 포인트백 O
    @Test
    public void 배송완료_포인트지급_및_알림발송() {
        // Given
        Long orderId = 101L;
        OrderDetails order = createOrderDetails(orderId, member1, "상품A", OrderStatus.DELIVERY, 1, 10000);
        when(orderDetailsRepository.findById(orderId)).thenReturn(Optional.of(order));

        when(productInventoryRepository.findProductByProductName("상품A")).thenReturn(product1);

        Point memberPoint = mock(Point.class);
        when(pointRepository.findByMember(member1)).thenReturn(Optional.of(memberPoint));

        // When
        orderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);

        // Then
        verify(order).updateOrderStatus(OrderStatus.COMPLETED);
        verify(orderDetailsRepository).save(order);
        verify(memberPoint).increasePoint(2000);
        verify(fcmFacade).sendMessage(NotifyMessage.PRODUCT_ARRIVAL, member1.getId());
        verify(fcmFacade).sendMessage(NotifyMessage.REWARD_POINT, member1.getId());
    }

    // DELIVERY → COMPLETED 포인트백 X
    @Test
    public void 배송완료_포인트백없는경우_알림만발송() {
        // Given
        Long orderId = 101L;
        OrderDetails order = createOrderDetails(orderId, member1, "상품A", OrderStatus.DELIVERY, 1, 10000);
        when(orderDetailsRepository.findById(orderId)).thenReturn(Optional.of(order));

        ProductInventory samePrice = mock(ProductInventory.class);
        when(samePrice.getProductName()).thenReturn("상품A");
        when(samePrice.getOriginalPrice()).thenReturn(10000);
        when(samePrice.getSaleRate()).thenReturn(BigDecimal.ZERO);
        when(samePrice.getCurrentDiscountRate()).thenReturn(BigDecimal.ZERO);

        when(productInventoryRepository.findProductByProductName("상품A")).thenReturn(samePrice);

        Point memberPoint = mock(Point.class);
        when(pointRepository.findByMember(member1)).thenReturn(Optional.of(memberPoint));

        // When
        orderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);

        // Then
        verify(order).updateOrderStatus(OrderStatus.COMPLETED);
        verify(orderDetailsRepository).save(order);
        verify(memberPoint, never()).increasePoint(anyInt());
        verify(fcmFacade).sendMessage(NotifyMessage.PRODUCT_ARRIVAL, member1.getId());
        verify(fcmFacade, never()).sendMessage(eq(NotifyMessage.REWARD_POINT), anyLong());
    }

    // READY → DELIVERY
    @Test
    public void 배송중_상태변경_포인트와_알림_없음() {
        // Given
        Long orderId = 101L;
        OrderDetails order = createOrderDetails(orderId, member1, "상품A", OrderStatus.READY, 1, 10000);
        when(orderDetailsRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        orderService.updateOrderStatus(orderId, OrderStatus.DELIVERY);

        // Then
        verify(order).updateOrderStatus(OrderStatus.DELIVERY);
        verify(orderDetailsRepository).save(order);
        verify(pointRepository, never()).increasePointByMemberId(anyLong(), anyInt());
        verify(fcmFacade, never()).sendMessage(any(NotifyMessage.class), anyLong());
    }
}