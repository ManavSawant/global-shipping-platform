package com.MNS.global_shipping_platform.sailing;

import com.MNS.global_shipping_platform.exception.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.exception.SailingNotFoundException;
import com.MNS.global_shipping_platform.exception.SailingNumberAlreadyExists;
import com.MNS.global_shipping_platform.exception.VesselNotFound;
import com.MNS.global_shipping_platform.vessel.Vessel;
import com.MNS.global_shipping_platform.vessel.VesselRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SailingService {
    private final SailingRepository sailingRepository;
    private final VesselRepository vesselRepository;

    public SailingResponse mapToSailingResponse(Sailing sailing) {
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
       Vessel vessel = null;
        if (request.vesselId() != null) {
            vessel = vesselRepository.findById(request.vesselId())
                    .orElseThrow(() -> new VesselNotFound("No Vessel found with id: " + request.vesselId()));
        }
        if(!request.arrivalTime().isAfter(request.departureTime())){
            throw new InvalidSailingScheduleException("Arrival time must be after departure time");
        }
        Sailing newSailing = Sailing.builder()
                .sailingNumber(request.sailingNumber())
                .vessel(vessel)
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
                .orElseThrow(()-> new VesselNotFound("No Vessel found with id: " + vesselId));

        sailing.setVessel(vessel);
        Sailing savedSailing = sailingRepository.save(sailing);
        return mapToSailingResponse(savedSailing);
    }



}
