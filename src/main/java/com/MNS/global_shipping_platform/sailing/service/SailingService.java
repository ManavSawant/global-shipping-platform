package com.MNS.global_shipping_platform.sailing.service;

import com.MNS.global_shipping_platform.exception.sailing.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.exception.sailing.SailingNotFoundException;
import com.MNS.global_shipping_platform.exception.sailing.SailingNumberAlreadyExists;
import com.MNS.global_shipping_platform.sailing.SailingStatus;
import com.MNS.global_shipping_platform.sailing.dto.SailingRequest;
import com.MNS.global_shipping_platform.sailing.dto.SailingResponse;
import com.MNS.global_shipping_platform.sailing.dto.UpdateSailingRequest;
import com.MNS.global_shipping_platform.sailing.entity.PortCall;
import com.MNS.global_shipping_platform.sailing.entity.Sailing;
import com.MNS.global_shipping_platform.sailing.repository.PortCallsRepository;
import com.MNS.global_shipping_platform.sailing.repository.SailingRepository;
import com.MNS.global_shipping_platform.exception.vessel.VesselNotFoundException;
import com.MNS.global_shipping_platform.vessel.VesselStatus;
import com.MNS.global_shipping_platform.vessel.entity.Vessel;
import com.MNS.global_shipping_platform.vessel.repository.VesselRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SailingService {
    private final SailingRepository sailingRepository;
    private final VesselRepository vesselRepository;
    private final PortCallsRepository portCallsRepository;

    private SailingResponse mapToSailingResponse(Sailing sailing) {
        return new SailingResponse(
                sailing.getId(),
                sailing.getSailingNumber(),
                sailing.getVessel() != null ? sailing.getVessel().getId() : null,
                sailing.getVessel() != null ? sailing.getVessel().getVesselName() : null,
                sailing.getDepartureTime(),
                sailing.getArrivalTime(),
                sailing.getStatus()
        );
    }

    public SailingResponse createSailing(SailingRequest request) {
        if(sailingRepository.existsBySailingNumber(request.sailingNumber())){
            throw new SailingNumberAlreadyExists(request.sailingNumber());
        }
        if(!request.arrivalTime().isAfter(request.departureTime())){
            throw new InvalidSailingScheduleException("Arrival time must be after departure time");
        }
        Sailing newSailing = Sailing.builder()
                .sailingNumber(request.sailingNumber())
                .departureTime(request.departureTime())
                .arrivalTime(request.arrivalTime())
                .status(SailingStatus.PLANNED)
                .build();
        Sailing savedSailing = sailingRepository.save(newSailing);
        return mapToSailingResponse(savedSailing);
    }

    public SailingResponse getSailingById(UUID id) {
        Sailing sailing = sailingRepository.findById(id)
                .orElseThrow(()-> new SailingNotFoundException("No Sailing found with id: " + id));
        return mapToSailingResponse(sailing);
    }

    public List<SailingResponse>  getAllSailings() {
        return sailingRepository.findAll()
                .stream()
                .map(this::mapToSailingResponse)
                .toList();
    }

    public SailingResponse assignSailingToVessel(UUID sailingId, UUID vesselId) {
        Sailing sailing = sailingRepository.findById(sailingId)
                .orElseThrow(()-> new SailingNotFoundException("No Sailing found with id: " + sailingId));

        if(sailing.getStatus() != SailingStatus.PLANNED) {
            throw new InvalidSailingScheduleException("Vessel can only be assigned or changed while sailing is PLANNED");
        }

        Vessel vessel = vesselRepository.findById(vesselId)
                .orElseThrow(()-> new VesselNotFoundException("No Vessel found with id: " + vesselId));

        if(sailing.getVessel() != null){
            throw new InvalidSailingScheduleException("A vessel has already been assigned to this Sailing");
        }

        sailing.setVessel(vessel);
        Sailing savedSailing = sailingRepository.save(sailing);
        return mapToSailingResponse(savedSailing);
    }

    public SailingResponse updateSailing(UUID id, UpdateSailingRequest sailingRequest) {
        Sailing sailing = sailingRepository.findById(id).orElseThrow(()-> new SailingNotFoundException("No Sailing found with id: " + id));

        if(sailing.getStatus() != SailingStatus.PLANNED) {
            throw new InvalidSailingScheduleException("Sailing can only be updated while its status is PLANNED.");
        }

        if(sailingRepository.existsBySailingNumberAndIdNot(sailingRequest.sailingNumber(), id)){
            throw new SailingNumberAlreadyExists("Sailing number already exists");
        }

        if(!sailingRequest.arrivalTime().isAfter(sailingRequest.departureTime())){
            throw new InvalidSailingScheduleException("Arrival time must be after departure time");
        }

        sailing.setSailingNumber(sailingRequest.sailingNumber());
        sailing.setDepartureTime(sailingRequest.departureTime());
        sailing.setArrivalTime(sailingRequest.arrivalTime());

        Sailing savedSailing = sailingRepository.save(sailing);
        return mapToSailingResponse(savedSailing);
    }

    public SailingResponse scheduleSailing(UUID sailingId) {
        Sailing sailing = sailingRepository.findById(sailingId)
                .orElseThrow(() -> new SailingNotFoundException("No Sailing found with id: " + sailingId));

        if(sailing.getStatus() != SailingStatus.PLANNED) {
            throw new InvalidSailingScheduleException("Only a PLANNED sailing can be scheduled.");
        }

        if (sailing.getVessel() == null) {
            throw new InvalidSailingScheduleException(
                    "A vessel must be assigned before scheduling the sailing.");
        }

        if(sailing.getVessel().getStatus() != VesselStatus.AVAILABLE) {
            throw new InvalidSailingScheduleException("Assigned vessel is not available for sailing.");
        }

        List<PortCall> portCalls = portCallsRepository.findBySailingIdOrderBySequenceNumberAsc(sailing.getId());
        if(portCalls.size() < 2) {
            throw new InvalidSailingScheduleException("A sailing must have at least an origin and destination port before it can be scheduled.");
        }
        for(int i = 0; i < portCalls.size(); i++) {
            PortCall portCall = portCalls.get(i);
            if(portCall.getSequenceNumber() != i + 1){
                throw new InvalidSailingScheduleException("Sequence numbers must be continuous starting from 1.");
            }
        }
        sailing.setStatus(SailingStatus.SCHEDULED);
        Sailing savedSailing = sailingRepository.save(sailing);
        return mapToSailingResponse(savedSailing);
    }

    public void deleteSailing(UUID id) {
        Sailing sailing = sailingRepository.findById(id)
                .orElseThrow(()-> new SailingNotFoundException("No Sailing found with id: " + id));
        sailingRepository.delete(sailing);
    }

}

