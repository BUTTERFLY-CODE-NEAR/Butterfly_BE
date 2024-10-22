package com.codenear.butterfly.member.domain;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.global.domain.BaseEntity;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.product.domain.Favorite;
import com.codenear.butterfly.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(nullable = false)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    private String password;

    @Column(nullable = false)
    @Setter
    private String nickname;

    @Setter
    private String profileImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Point point;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Consent> consents = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    public void setPoint(Point point) {
        this.point = point;
        point.setMember(this);
    }

    public void addFavorite(Product product) {
        Favorite favorite = Favorite.createFavorite(this, product);
        this.favorites.add(favorite);
    }

    public void removeFavorite(Product product) {
        this.favorites.removeIf(favorite -> favorite.getProduct().equals(product));
    }
}