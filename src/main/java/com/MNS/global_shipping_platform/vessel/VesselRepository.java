package com.MNS.global_shipping_platform.vessel;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface VesselRepository extends CrudRepository<Vessel, UUID> {
}
