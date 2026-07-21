package com.MNS.global_shipping_platform.sailing;

import com.MNS.global_shipping_platform.port.Port;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_sailing_port_sequence",
                        columnNames = {"sailing_id", "sequence_number"}
                )
        }
)
public class PortCall {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sailing_id", nullable = false)
    private Sailing sailing;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "port_id", nullable = false)
    private Port port;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    private LocalDateTime arrivalTime;

    private LocalDateTime departureTime;
}