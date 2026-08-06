package com.MNS.global_shipping_platform.port.service;

import com.MNS.global_shipping_platform.exception.port.PortAlreadyExistExceptions;
import com.MNS.global_shipping_platform.exception.port.PortNotFoundException;
import com.MNS.global_shipping_platform.port.PortStatus;
import com.MNS.global_shipping_platform.port.dto.PortRequest;
import com.MNS.global_shipping_platform.port.dto.PortResponse;
import com.MNS.global_shipping_platform.port.entity.Port;
import com.MNS.global_shipping_platform.port.repository.PortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortService {

    private final PortRepository portRepository;

    private PortResponse mapToResponse(Port port){
        return new PortResponse(
                port.getId(),
                port.getLocode(),
                port.getPortName(),
                port.getCity(),
                port.getCountry(),
                port.getStatus(),
                port.getLatitude(),
                port.getLongitude()
        );

    }

    public PortResponse createPort(PortRequest requestDto){
        String inputLocode = requestDto.locode();
        if(portRepository.existsByLocode(inputLocode)){
            throw new PortAlreadyExistExceptions("Port with locode " + inputLocode + " already exists");
        }
        if(portRepository.existsByPortNameIgnoreCaseAndCountryIgnoreCase(requestDto.portName(), requestDto.country())){
            throw new PortAlreadyExistExceptions("Port already exists");
        }
        Port port = Port.builder()
                .locode(inputLocode)
                .portName(requestDto.portName())
                .city(requestDto.city())
                .country(requestDto.country())
                .status(PortStatus.ACTIVE)
                .latitude(requestDto.latitude())
                .longitude(requestDto.longitude())
                .build();
        Port savedPort = portRepository.save(port);
        return mapToResponse(savedPort);
    }

    public PortResponse getPortById(UUID portId){
        Port port = portRepository.findById(portId)
                .orElseThrow(()-> new PortNotFoundException("Port with id " + portId + " not found"));
        return mapToResponse(port);
    }

    public List<PortResponse> getAllPorts(){
        return portRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PortResponse updatePort(UUID portId, PortRequest requestDto){
        Port port = portRepository.findById(portId)
                .orElseThrow(()-> new PortNotFoundException("Port with id " + portId + " not found"));

       if(portRepository.existsByPortNameIgnoreCaseAndCountryIgnoreCaseAndIdNot(requestDto.portName(), requestDto.country(),  portId)){
           throw new PortAlreadyExistExceptions("Port with id " + portId + " already exists in"+requestDto.portName()+" and "+requestDto.country());
       }

        port.setPortName(requestDto.portName());
        port.setCity(requestDto.city());
        port.setCountry(requestDto.country());
        port.setLatitude(requestDto.latitude());
        port.setLongitude(requestDto.longitude());

        Port savedPort = portRepository.save(port);
        return mapToResponse(savedPort);
    }

    public void deletePortById(UUID portId){
        Port port = portRepository.findById(portId)
                .orElseThrow(()-> new PortNotFoundException("Port with id " + portId + " not found"));

        portRepository.delete(port);
    }

}
