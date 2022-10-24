package com.gfa.homework.services;

import com.gfa.homework.models.dtos.CustomerDto;
import com.gfa.homework.models.dtos.UsernamePasswordDto;
import com.gfa.homework.models.entities.Customer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomerService {
  Customer saveNewCustomer(UsernamePasswordDto dto);

  CustomerDto convertToDto(Customer customer);

  boolean customerExists(UsernamePasswordDto dto);

  Customer getCustomerByUsername(String username);

  UserDetails getUserDetailsByUsername(String username) throws UsernameNotFoundException;

  String getToken(UserDetails userDetails);

  Customer getCustomerFromAuthorizationHeader(String bearerToken);
}
