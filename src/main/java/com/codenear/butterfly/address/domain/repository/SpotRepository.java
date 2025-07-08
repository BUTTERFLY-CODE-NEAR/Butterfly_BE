package com.codenear.butterfly.address.domain.repository;

import com.codenear.butterfly.address.domain.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<Spot, Long> {
}
