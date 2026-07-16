package com.MNS.global_shipping_platform.customer;

import com.MNS.global_shipping_platform.exception.CustomerAlreadyExistsException;
import com.MNS.global_shipping_platform.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponse mapToResponseDTO(Customer customer) {
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

    public CustomerResponse findByCustomerId(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("No Customer found with id: " + customerId));

        return mapToResponseDTO(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream().map(this::mapToResponseDTO) //.map(customer -> mapToResponseDTO(customer))
                .toList();
    }

    public CustomerResponse updateCustomer(UUID customerId, CustomerRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("No Customer found with id: " + customerId));

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
}
