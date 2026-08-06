package com.MNS.global_shipping_platform.vessel.repository;

import com.MNS.global_shipping_platform.vessel.entity.Vessel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VesselRepository extends JpaRepository<Vessel, UUID> {

    boolean existsByImoNumber(String imoNumber);

    Optional<Vessel> findByImoNumber(String imoNumber);

}
