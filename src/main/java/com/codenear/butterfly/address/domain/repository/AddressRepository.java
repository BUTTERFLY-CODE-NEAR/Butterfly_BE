package com.codenear.butterfly.address.domain.repository;

import com.codenear.butterfly.address.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.LinkedList;

public interface AddressRepository extends JpaRepository<Address, Long> {
    LinkedList<Address> findAllByMemberId(Long memberId);
}