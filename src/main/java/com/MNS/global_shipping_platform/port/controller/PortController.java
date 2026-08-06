package com.MNS.global_shipping_platform.port.controller;

import com.MNS.global_shipping_platform.port.dto.PortRequest;
import com.MNS.global_shipping_platform.port.dto.PortResponse;
import com.MNS.global_shipping_platform.port.service.PortService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ports")
public class PortController {
    private final PortService portService;

    @PostMapping
    public ResponseEntity<PortResponse> createPort (@Valid @RequestBody PortRequest requestDto){
        PortResponse response = portService.createPort(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{portId}")
    public ResponseEntity<PortResponse> getPortById(@PathVariable UUID portId){
        PortResponse response = portService.getPortById(portId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PortResponse>> getAllPorts(){
        List<PortResponse> response = portService.getAllPorts();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{portId}")
    public ResponseEntity<PortResponse> updatePort(@PathVariable UUID portId, @Valid @RequestBody PortRequest requestDto){
        PortResponse response = portService.updatePort(portId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{portId}")
    public ResponseEntity<Void> deletePortById(@PathVariable UUID portId){
        portService.deletePortById(portId);
        return ResponseEntity.noContent().build();
    }
}
