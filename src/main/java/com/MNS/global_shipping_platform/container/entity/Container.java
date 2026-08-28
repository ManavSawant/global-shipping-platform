package com.MNS.global_shipping_platform.container.entity;

import com.MNS.global_shipping_platform.booking.entity.Booking;
import com.MNS.global_shipping_platform.container.ContainerStatus;
import com.MNS.global_shipping_platform.container.ContainerType;
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
public class Container {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Enumerated(EnumType.STRING)
    private ContainerType containerType;

    private String containerNumber;

    private Integer size;

    private Integer weight;

    @Enumerated(EnumType.STRING)
    private ContainerStatus containerStatus;

    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
