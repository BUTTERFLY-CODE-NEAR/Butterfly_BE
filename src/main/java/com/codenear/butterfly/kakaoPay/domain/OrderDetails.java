package com.codenear.butterfly.kakaoPay.domain;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderType;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Setter
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    // 직거래 시
    private String pickupPlace;
    private LocalDate pickupDate;
    private LocalTime pickupTime;

    // 배달 시
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
    private LocalDate deliverDate;
}
