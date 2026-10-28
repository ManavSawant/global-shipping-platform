package com.MNS.global_shipping_platform.customer.entity;

import com.MNS.global_shipping_platform.customer.CustomerType;
import com.MNS.global_shipping_platform.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String companyName;

    private String contactPersonFirstName;

    private String contactPersonLastName;

    @Email
    @NotBlank
    @Column(unique = true)
    private String customerEmail;

    @NotBlank
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
