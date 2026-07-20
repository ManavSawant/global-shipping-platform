package com.MNS.global_shipping_platform.port;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Port {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false , unique = true , name = "LOCODE")
    private String locode;

    @Column(nullable = false)
    private String portName;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PortStatus status;

    @Column(nullable = false , precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false , precision = 9, scale = 6)
    private BigDecimal longitude;
}
