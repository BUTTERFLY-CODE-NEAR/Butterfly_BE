package com.codenear.butterfly.address.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByMemberId(Long memberId);
}