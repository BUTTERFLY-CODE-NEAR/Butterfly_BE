package com.codenear.butterfly.product.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productImage;

    @Column(nullable = false)
    private String subtitle;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer originalPrice;

    @Column(nullable = false, precision = 4, scale = 1)
    private BigDecimal saleRate;

}