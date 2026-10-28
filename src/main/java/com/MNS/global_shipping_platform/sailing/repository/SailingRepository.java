package com.MNS.global_shipping_platform.sailing.repository;

import com.MNS.global_shipping_platform.sailing.entity.Sailing;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SailingRepository extends JpaRepository<Sailing, UUID> {

    boolean existsBySailingNumber(String sailingNumber);

    boolean existsBySailingNumberAndIdNot(String sailingNumber, UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Sailing> findByIdForUpdate(@Param("id") UUID id);

}
