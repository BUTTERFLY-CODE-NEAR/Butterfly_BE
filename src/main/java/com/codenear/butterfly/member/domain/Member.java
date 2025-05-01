package com.codenear.butterfly.member.domain;

import com.codenear.butterfly.address.domain.Address;
import com.codenear.butterfly.consent.domain.Consent;
import com.codenear.butterfly.global.domain.BaseEntity;
import com.codenear.butterfly.notify.alarm.domain.Alarm;
import com.codenear.butterfly.notify.alarm.domain.Restock;
import com.codenear.butterfly.point.domain.Point;
import com.codenear.butterfly.product.domain.Favorite;
import com.codenear.butterfly.product.domain.Product;
import com.codenear.butterfly.product.domain.ProductInventory;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

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
    @Setter
    private String phoneNumber;

    private String password;

    @Column(unique = true, nullable = false)
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Restock> restocks = new ArrayList<>();

    @JsonProperty("deleted")
    private boolean isDeleted;

    public void setPoint(Point point) {
        this.point = point;
        point.setMember(this);
    }

    public void addFavorite(ProductInventory product) {
        Favorite favorite = Favorite.createFavorite(this, product);
        this.favorites.add(favorite);
    }

    public void removeFavorite(Product product) {
        this.favorites.removeIf(favorite -> favorite.getProduct().equals(product));
    }

    public boolean hasFavorite(Product product) {
        return favorites.stream()
                .anyMatch(favorite -> favorite.getProduct().equals(product));
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void withdraw() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public void addRestock(Restock restock) {
        this.restocks.add(restock);
    }
}