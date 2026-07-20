package com.MNS.global_shipping_platform.voyage;

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
                        name = "uk_voyage_port_sequence",
                        columnNames = {"voyage_id", "sequence_number"}
                )
        }
)
public class VoyagePortCall {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voyage_id", nullable = false)
    private Voyage voyage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "port_id", nullable = false)
    private Port port;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    private LocalDateTime arrivalTime;

    private LocalDateTime departureTime;
}