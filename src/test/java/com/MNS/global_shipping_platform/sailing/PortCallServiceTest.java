package com.MNS.global_shipping_platform.sailing;

import com.MNS.global_shipping_platform.exception.portCalls.InvalidPortCallException;
import com.MNS.global_shipping_platform.port.entity.Port;
import com.MNS.global_shipping_platform.port.repository.PortRepository;
import com.MNS.global_shipping_platform.sailing.dto.PortCallRequest;
import com.MNS.global_shipping_platform.sailing.entity.Sailing;
import com.MNS.global_shipping_platform.sailing.repository.PortCallsRepository;
import com.MNS.global_shipping_platform.sailing.repository.SailingRepository;
import com.MNS.global_shipping_platform.sailing.service.PortCallService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortCallServiceTest {

    @Mock
    private PortCallsRepository portCallsRepository;

    @Mock
    private SailingRepository sailingRepository;

    @Mock
    private PortRepository portRepository;

    @InjectMocks
    private PortCallService portCallService;

    @Test
    void createPortCall_shouldRejectNullOrNonPositiveSequenceNumber() {
        UUID sailingId = UUID.randomUUID();
        UUID portId = UUID.randomUUID();
        Sailing sailing = Sailing.builder()
                .id(sailingId)
                .status(SailingStatus.PLANNED)
                .build();
        Port port = new Port();
        port.setId(portId);

        when(sailingRepository.findById(sailingId))
                .thenReturn(Optional.of(sailing));
        when(portRepository.findById(portId))
                .thenReturn(Optional.of(port));

        PortCallRequest missingSequence =
                new PortCallRequest(sailingId, portId, null, null, null);
        PortCallRequest zeroSequence =
                new PortCallRequest(sailingId, portId, 0, null, null);

        assertThrows(InvalidPortCallException.class,
                () -> portCallService.createPortCall(missingSequence));
        assertThrows(InvalidPortCallException.class,
                () -> portCallService.createPortCall(zeroSequence));

        verifyNoInteractions(portCallsRepository);
    }
}
