package com.viehai.identity_service.identity.domain.repository;

import com.viehai.identity_service.identity.domain.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
