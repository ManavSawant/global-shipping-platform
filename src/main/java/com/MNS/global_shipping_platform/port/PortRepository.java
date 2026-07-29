package com.MNS.global_shipping_platform.port;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.UUID;

public interface PortRepository extends JpaRepositoryImplementation<Port, UUID> {
}
