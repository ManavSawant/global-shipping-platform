package com.MNS.global_shipping_platform.sailing.service;

import com.MNS.global_shipping_platform.exception.port.PortNotFoundException;
import com.MNS.global_shipping_platform.port.entity.Port;
import com.MNS.global_shipping_platform.port.repository.PortRepository;
import com.MNS.global_shipping_platform.exception.sailing.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.exception.sailing.SailingNotFoundException;
import com.MNS.global_shipping_platform.sailing.SailingStatus;
import com.MNS.global_shipping_platform.sailing.dto.PortCallRequest;
import com.MNS.global_shipping_platform.sailing.dto.PortCallResponse;
import com.MNS.global_shipping_platform.sailing.entity.PortCall;
import com.MNS.global_shipping_platform.sailing.entity.Sailing;
import com.MNS.global_shipping_platform.sailing.repository.PortCallsRepository;
import com.MNS.global_shipping_platform.sailing.repository.SailingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortCallService {
    private final PortCallsRepository portCallsRepository;
    private final SailingRepository sailingRepository;
    private final PortRepository portRepository;

    public PortCallResponse mapToPortCallResponse(PortCall portCall) {
        return new PortCallResponse(
                portCall.getId(),
                portCall.getSailing().getId(),
                portCall.getPort().getId(),
                portCall.getPort().getPortName(),
                portCall.getPort().getLocode(),
                portCall.getSequenceNumber(),
                portCall.getDepartureTime(),
                portCall.getArrivalTime()

        );
    }


    public PortCallResponse createPortCall(PortCallRequest portCallRequest) {

        Sailing sailing = sailingRepository.findById(portCallRequest.sailingId())
                .orElseThrow(() -> new SailingNotFoundException("Sailing not found"));

        if (sailing.getStatus() != SailingStatus.PLANNED){
            throw new InvalidSailingScheduleException("Invalid sailing schedule");
        }
        Port port = portRepository.findById(portCallRequest.portId())
                .orElseThrow(()-> new PortNotFoundException("Port not found with id " + portCallRequest.portId()));

        if (portCallRequest.sequenceNumber() <= 0){
            throw new RuntimeException("Sequence number must be greater than zero");
        }

        if (portCallsRepository.existsBySailingIdAndSequenceNumber(
                portCallRequest.sailingId(), portCallRequest.sequenceNumber())) {
            throw new InvalidSailingScheduleException(
                    "Sequence number " + portCallRequest.sequenceNumber() + " already exists for this sailing"
            );
        }

        if (portCallRequest.arrivalTime() != null
                && portCallRequest.departureTime() != null
                && !portCallRequest.departureTime().isAfter(portCallRequest.arrivalTime())) {
            throw new InvalidSailingScheduleException("Departure date must be after arrival date");
        }

        PortCall portcall = PortCall.builder()
                .sailing(sailing)
                .port(port)
                .sequenceNumber(portCallRequest.sequenceNumber())
                .departureTime(portCallRequest.departureTime())
                .arrivalTime(portCallRequest.arrivalTime())
                .build();

        PortCall savedPortCall = portCallsRepository.save(portcall);
        return mapToPortCallResponse(savedPortCall);
    }

    public List<PortCallResponse> getAllPortCallsBySailingId(UUID sailingId) {
        Sailing sailing = sailingRepository.findById(sailingId)
                .orElseThrow(() -> new SailingNotFoundException("Sailing not found"));
        return portCallsRepository.findBySailingIdOrderBySequenceNumberAsc(sailingId)
                .stream()
                .map(this::mapToPortCallResponse)
                .toList();
    }
}
