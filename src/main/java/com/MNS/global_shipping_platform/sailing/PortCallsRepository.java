package com.MNS.global_shipping_platform.sailing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PortCallsRepository extends JpaRepository<PortCall, UUID> {

  public boolean existsBySailingIdAndSequenceNumber(UUID sailingId, Integer sequenceNumber);

  List<PortCall> findBySailingIdOrderBySequenceNumberAsc(UUID sailingId);
}
