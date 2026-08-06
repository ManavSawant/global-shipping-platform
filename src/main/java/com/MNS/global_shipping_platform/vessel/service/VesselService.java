package com.MNS.global_shipping_platform.vessel.service;

import com.MNS.global_shipping_platform.exception.vessel.VesselAlreadyExistsException;
import com.MNS.global_shipping_platform.exception.vessel.VesselNotFoundException;
import com.MNS.global_shipping_platform.vessel.*;
import com.MNS.global_shipping_platform.vessel.dto.VesselRequest;
import com.MNS.global_shipping_platform.vessel.dto.VesselResponse;
import com.MNS.global_shipping_platform.vessel.entity.Vessel;
import com.MNS.global_shipping_platform.vessel.repository.VesselRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VesselService {
    private final VesselRepository vesselRepository;

    private VesselResponse mapToResponseDto(Vessel vessel) {
       return new VesselResponse(
               vessel.getId(),
               vessel.getImoNumber(),
               vessel.getVesselName(),
               vessel.getCapacityTeu(),
               vessel.getBuiltYear(),
               vessel.getStatus(),
               vessel.getCreatedDate(),
               vessel.getUpdatedDate()
       );
    }

    public VesselResponse createVessel(VesselRequest requestDto) {

        if(vesselRepository.existsByImoNumber(requestDto.imoNumber())) {
            throw new VesselAlreadyExistsException("Vessel with imoNumber " + requestDto.imoNumber() + " already exists");
        }

        Vessel vessel = Vessel.builder()
                .imoNumber(requestDto.imoNumber())
                .vesselName(requestDto.vesselName())
                .capacityTeu(requestDto.capacityTeu())
                .builtYear(requestDto.builtYear())
                .status(VesselStatus.AVAILABLE)
                .build();

        Vessel savedVessel = vesselRepository.save(vessel);
        return mapToResponseDto(savedVessel);
    }

    public VesselResponse getVesselById(UUID id) {
        Vessel vessel = vesselRepository.findById(id)
                .orElseThrow(() -> new VesselNotFoundException("Vessel with id " + id + " not found"));

        return mapToResponseDto(vessel);
    }

    public List<VesselResponse> getAllVessels() {
        return vesselRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public VesselResponse updateVessel(UUID id, VesselRequest requestDto) {
        Vessel vessel = vesselRepository.findById(id)
                .orElseThrow(() -> new VesselNotFoundException("Vessel with id " + id + " not found"));

        vessel.setVesselName(requestDto.vesselName());
        vessel.setCapacityTeu(requestDto.capacityTeu());
        vessel.setBuiltYear(requestDto.builtYear());

        Vessel savedVessel = vesselRepository.save(vessel);
        return mapToResponseDto(savedVessel);

    }

    public void deleteVessel(UUID id) {
        Vessel vessel = vesselRepository.findById(id)
                .orElseThrow(() -> new VesselNotFoundException("Vessel with id " + id + " not found"));
        vesselRepository.delete(vessel);
    }
}
