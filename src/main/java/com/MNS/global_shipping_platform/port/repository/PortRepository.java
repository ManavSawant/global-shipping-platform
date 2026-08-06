package com.MNS.global_shipping_platform.port.repository;

import com.MNS.global_shipping_platform.port.entity.Port;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.UUID;

public interface PortRepository extends JpaRepositoryImplementation<Port, UUID> {

    boolean existsByLocode(String locode);

    boolean existsByPortNameIgnoreCaseAndCountryIgnoreCase(String portName, String country);

    boolean existsByPortNameIgnoreCaseAndCountryIgnoreCaseAndIdNot(String portName, String country, UUID id);

}
