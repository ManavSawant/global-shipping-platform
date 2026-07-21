package com.MNS.global_shipping_platform.sailing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SailingRepository extends JpaRepository<Sailing, UUID> {

    public boolean existsBySailingNumber(String sailingNumber);
}
