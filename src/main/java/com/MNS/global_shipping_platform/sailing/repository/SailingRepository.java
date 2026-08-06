package com.MNS.global_shipping_platform.sailing.repository;

import com.MNS.global_shipping_platform.sailing.entity.Sailing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SailingRepository extends JpaRepository<Sailing, UUID> {

    boolean existsBySailingNumber(String sailingNumber);

    boolean existsBySailingNumberAndIdNot(String sailingNumber, UUID id);

}
