package com.MNS.global_shipping_platform.sailing;

import com.MNS.global_shipping_platform.exception.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.exception.PortNotFoundException;
import com.MNS.global_shipping_platform.exception.SailingNotFoundException;
import com.MNS.global_shipping_platform.port.Port;
import com.MNS.global_shipping_platform.port.PortRepository;
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

        Sailing sailing = sailingRepository.findById(portCallRequest.sailing_id())
                .orElseThrow(() -> new SailingNotFoundException("Sailing not found"));

        if (sailing.getStatus() != SailingStatus.PLANNED){
            throw new InvalidSailingScheduleException("Invalid sailing schedule");
        }
        Port port = portRepository.findById(portCallRequest.port_id())
                .orElseThrow(()-> new PortNotFoundException("Port not found with id " + portCallRequest.port_id()));

        if (portCallRequest.sequence_number() <= 0){
            throw new RuntimeException("Sequence number must be greater than zero");
        }

        if (portCallsRepository.existsBySailingIdAndSequenceNumber(
                portCallRequest.sailing_id(), portCallRequest.sequence_number())) {
            throw new InvalidSailingScheduleException(
                    "Sequence number " + portCallRequest.sequence_number() + " already exists for this sailing"
            );
        }

        if (portCallRequest.arrival_date() != null
                && portCallRequest.departure_date() != null
                && !portCallRequest.departure_date().isAfter(portCallRequest.arrival_date())) {
            throw new InvalidSailingScheduleException("Departure date must be after arrival date");
        }

        PortCall portcall = PortCall.builder()
                .sailing(sailing)
                .port(port)
                .sequenceNumber(portCallRequest.sequence_number())
                .departureTime(portCallRequest.departure_date())
                .arrivalTime(portCallRequest.arrival_date())
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
