package com.codenear.butterfly.kakaoPay.domain;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderType;
import com.codenear.butterfly.kakaoPay.domain.dto.kakao.ApproveResponseDTO;
import com.codenear.butterfly.member.domain.Member;
import com.codenear.butterfly.product.domain.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(length = 16)
    private String orderCode;

    private String tid;
    private LocalDateTime createdAt;

    // 직거래 시
    private String pickupPlace;
    private LocalDate pickupDate;
    private LocalTime pickupTime;

    // 배달 시
    private String address;
    private String detailedAddress;
    private LocalDate deliverDate;

    private String productName;
    private String productImage;
    private String optionName;
    private Integer total;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Builder
    public OrderDetails(Member member, OrderType orderType, ApproveResponseDTO approveResponseDTO, Product product, String optionName) {
        this.member = member;
        this.orderType = orderType;
        this.orderCode = generateOrderCode();
        this.createdAt = LocalDateTime.parse(approveResponseDTO.getCreated_at());
        this.tid = approveResponseDTO.getTid();
        this.total = approveResponseDTO.getAmount().getTotal();
        this.productName = approveResponseDTO.getItem_name();
        this.productImage = product.getProductImage();
        this.optionName = optionName;
        this.quantity = approveResponseDTO.getQuantity();
        this.orderStatus = OrderStatus.READY;

    }

    public void addOrderTypeByPickup(String pickupPlace, LocalDate pickupDate, LocalTime pickupTime) {
        this.pickupPlace = pickupPlace;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
    }

    public void addOrderTypeByDeliver(Address address) {
        this.address = address.getAddress();
        this.detailedAddress = address.getDetailedAddress();
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    private String generateOrderCode() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSSS");
        return now.format(formatter);
    }
}
