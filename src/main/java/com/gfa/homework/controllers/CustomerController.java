package com.gfa.homework.controllers;

import com.gfa.homework.exceptions.CustomerAlreadyExistsException;
import com.gfa.homework.exceptions.IncorrectPasswordException;
import com.gfa.homework.exceptions.NoSuchCustomerException;
import com.gfa.homework.models.dtos.BearerTokenDto;
import com.gfa.homework.models.dtos.ResponseDto;
import com.gfa.homework.models.dtos.UsernamePasswordDto;
import com.gfa.homework.models.entities.Customer;
import com.gfa.homework.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping("/sign-up")
  public ResponseEntity<ResponseDto> signUp(@RequestBody @Valid UsernamePasswordDto dto) {
    if (customerService.customerExists(dto)) {
      throw new CustomerAlreadyExistsException("/sign-up");
    }
    Customer customer = customerService.saveNewCustomer(dto);
    return ok(customerService.convertToDto(customer));
  }

  @PostMapping("/login")
  public ResponseEntity<ResponseDto> attemptLogin(@RequestBody @Valid UsernamePasswordDto dto) {
    if (!customerService.customerExists(dto)) {
      throw new NoSuchCustomerException("/login");
    }
    if (!dto.getPassword()
        .equals(customerService.getCustomerByUsername(dto.getUsername()).getPassword())) {
      throw new IncorrectPasswordException("/login");
    }
    User user = (User) customerService.getUserDetailsByUsername(dto.getUsername());
    String token = customerService.getToken(user);
    return ok(new BearerTokenDto(token));
  }
}
