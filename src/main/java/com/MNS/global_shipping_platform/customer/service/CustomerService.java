package com.MNS.global_shipping_platform.customer.service;

import com.MNS.global_shipping_platform.exception.customer.CustomerAlreadyExistsException;
import com.MNS.global_shipping_platform.exception.customer.CustomerNotFoundException;
import com.MNS.global_shipping_platform.customer.dto.CustomerRequest;
import com.MNS.global_shipping_platform.customer.dto.CustomerResponse;
import com.MNS.global_shipping_platform.customer.entity.Customer;
import com.MNS.global_shipping_platform.customer.repository.CustomerRepository;
import com.MNS.global_shipping_platform.user.UserRole;
import com.MNS.global_shipping_platform.user.entity.User;
import com.MNS.global_shipping_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private CustomerResponse mapToResponseDTO(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCompanyName(),
                customer.getContactPersonFirstName(),
                customer.getContactPersonLastName(),
                customer.getCustomerEmail(),
                customer.getPhoneNumber(),
                customer.getCustomerType(),
                customer.getCreatedDate()
        );

    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        Optional<Customer> customer = customerRepository.findByCustomerEmail(request.customerEmail());
        if (customer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already exists: " + request.customerEmail());
        }

        Customer newCustomer =  Customer.builder()
                .companyName(request.companyName())
                .contactPersonFirstName(request.contactPersonFirstName())
                .contactPersonLastName(request.contactPersonLastName())
                .customerEmail(request.customerEmail())
                .phoneNumber(request.phoneNumber())
                .customerType(request.customerType())
                .build();
        Customer savedCustomer = customerRepository.save(newCustomer);
        return mapToResponseDTO(savedCustomer);
    }

    public CustomerResponse findByCustomerId(UUID customerId, String email) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("No Customer found with id: " + customerId));

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(
                        "No Customer found with email: " + email));

        if(user.getRole() == UserRole.CUSTOMER && !customer.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Your are not allowed to access this customer");
        }

        return mapToResponseDTO(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream().map(this::mapToResponseDTO) //.map(customer -> mapToResponseDTO(customer))
                .toList();
    }

    public CustomerResponse updateCustomer(UUID customerId, CustomerRequest request, String email) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("No Customer found with id: " + customerId));

        User user = userRepository.findByEmail(email).orElseThrow(() ->new UsernameNotFoundException("No User found with email: " + email));

        if(user.getRole() == UserRole.CUSTOMER && !customer.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Your are not allowed to access this customer");
        }


        customerRepository.findByCustomerEmail(request.customerEmail())
                .ifPresent(existingCustomerWithEmail  -> {
                    if (!existingCustomerWithEmail.getId().equals(customerId)) {
                        throw new CustomerAlreadyExistsException("Customer already exists with email: " + request.customerEmail());
                    }
        });
        customer.setCompanyName(request.companyName());
        customer.setContactPersonFirstName(request.contactPersonFirstName());
        customer.setContactPersonLastName(request.contactPersonLastName());
        customer.setCustomerEmail(request.customerEmail());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setCustomerType(request.customerType());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponseDTO(updatedCustomer);
    }

    public void deleteCustomer(UUID customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() ->
                new CustomerNotFoundException("No Customer found with id: " + customerId));
        customerRepository.delete(customer);

    }

    public CustomerResponse createMyCustomerProfile(
            CustomerRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email));

        Optional<Customer> existingCustomer =
                customerRepository.findByUser(user);

        if (existingCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException(
                    "Customer profile already exists for: " + email);
        }

        Customer newCustomer = Customer.builder()
                .companyName(request.companyName())
                .contactPersonFirstName(request.contactPersonFirstName())
                .contactPersonLastName(request.contactPersonLastName())
                .customerEmail(request.customerEmail())
                .phoneNumber(request.phoneNumber())
                .customerType(request.customerType())
                .user(user)
                .build();

        Customer savedCustomer = customerRepository.save(newCustomer);

        return mapToResponseDTO(savedCustomer);
    }
}
