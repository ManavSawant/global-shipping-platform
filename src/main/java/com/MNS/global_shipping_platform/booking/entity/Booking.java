package com.MNS.global_shipping_platform.booking.entity;

import com.MNS.global_shipping_platform.booking.BookingStatus;
import com.MNS.global_shipping_platform.container.entity.Container;
import com.MNS.global_shipping_platform.customer.entity.Customer;
import com.MNS.global_shipping_platform.port.entity.Port;
import com.MNS.global_shipping_platform.sailing.entity.Sailing;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sailing_id")
    private Sailing sailing;

    @OneToMany(mappedBy = "booking")
    private List<Container> containers;

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
