package com.MNS.global_shipping_platform.booking;

import com.MNS.global_shipping_platform.customer.Customer;
import com.MNS.global_shipping_platform.port.Port;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "origin_port_id", nullable = false)
    private Port originPort;

    @ManyToOne
    @JoinColumn(name = "destination_port_id", nullable = false)
    private Port destinationPort;

    private String cargoType;

    private Integer cargoWeight;

    private Integer numberOfContainers;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @CreationTimestamp
    private LocalDateTime createDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;


}
