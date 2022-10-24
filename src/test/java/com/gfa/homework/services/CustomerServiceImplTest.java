package com.gfa.homework.services;

import com.gfa.homework.models.dtos.UsernamePasswordDto;
import com.gfa.homework.models.entities.Customer;
import com.gfa.homework.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CustomerServiceImplTest {

  private CustomerService customerService;
  private CustomerRepository customerRepository;

  @BeforeEach
  void init() {
    customerRepository = Mockito.mock(CustomerRepository.class);
    customerService = new CustomerServiceImpl(customerRepository, new ModelMapper());
  }

  @Test
  void can_save_new_customer() {
    UsernamePasswordDto usernamePasswordDto = new UsernamePasswordDto("username", "password");
    customerService.saveNewCustomer(usernamePasswordDto);
    verify(customerRepository, times(1)).save(any());
  }

  @Test
  void can_convert_to_dto() {
    Customer customer = new Customer("username", "password");
    assertNotNull(customerService.convertToDto(customer));
  }

  @Test
  void can_check_if_customer_exists() {
    when(customerRepository.existsCustomerByUsername("username")).thenReturn(true);
    assertTrue(customerService.customerExists(new UsernamePasswordDto("username", "password")));
  }

  @Test
  void can_get_customer_by_username() {
    when(customerRepository.getCustomerByUsername("username"))
        .thenReturn(new Customer("username", "password"));
    assertEquals("username", customerService.getCustomerByUsername("username").getUsername());
  }

  @Test
  void can_get_user_details_by_username() {
    when(customerRepository.getCustomerByUsername("username"))
        .thenReturn(new Customer("username", "password"));
    assertEquals("username", customerService.getUserDetailsByUsername("username").getUsername());
  }

  @Test
  void can_create_token() {
    when(customerRepository.getCustomerByUsername("username"))
        .thenReturn(new Customer("username", "password"));
    UserDetails userDetails = customerService.getUserDetailsByUsername("username");
    assertNotNull(customerService.getToken(userDetails));
  }

  @Test
  void can_get_customer_from_authorization_header() {
    when(customerRepository.getCustomerByUsername("username"))
        .thenReturn(new Customer("username", "password"));
    UserDetails userDetails = customerService.getUserDetailsByUsername("username");
    String token = "Bearer " + customerService.getToken(userDetails);
    assertNotNull(customerService.getCustomerFromAuthorizationHeader(token));
  }
}
