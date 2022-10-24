package com.gfa.homework.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gfa.homework.exceptions.NoSuchCustomerException;
import com.gfa.homework.models.dtos.CustomerDto;
import com.gfa.homework.models.dtos.UsernamePasswordDto;
import com.gfa.homework.models.entities.Customer;
import com.gfa.homework.repositories.CustomerRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.gfa.homework.security.SecurityConfiguration.TOKEN_EXPIRATION_TIME;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final ModelMapper modelMapper;

  @Override
  public Customer saveNewCustomer(UsernamePasswordDto dto) {
    return customerRepository.save(new Customer(dto.getUsername(), dto.getPassword()));
  }

  @Override
  public CustomerDto convertToDto(Customer customer) {
    return modelMapper.map(customer, CustomerDto.class);
  }

  @Override
  public boolean customerExists(UsernamePasswordDto dto) {
    return customerRepository.existsCustomerByUsername(dto.getUsername());
  }

  @Override
  public Customer getCustomerByUsername(String username) {
    Customer customer = customerRepository.getCustomerByUsername(username);
    if (customer == null) {
      throw new NoSuchCustomerException("Customer service: getCustomerByUsername()");
    }
    return customer;
  }

  @Override
  public UserDetails getUserDetailsByUsername(String username) throws UsernameNotFoundException {
    Customer customer = customerRepository.getCustomerByUsername(username);
    if (customer == null) {
      throw new NoSuchCustomerException("Customer service: getUserDetailsByUsername()");
    }
    return new org.springframework.security.core.userdetails.User(
        customer.getUsername(), customer.getPassword(), new ArrayList<>());
  }

  @Override
  public String getToken(UserDetails userDetails) {
    Dotenv dotenv = Dotenv.load();
    Algorithm algorithm =
        Algorithm.HMAC512(
            Objects.requireNonNull(dotenv.get("JWT_SECRET_KEY")).getBytes(StandardCharsets.UTF_8));
    return JWT.create()
        .withSubject(userDetails.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
        .withIssuer(
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/customers/login")
                .toString())
        .withClaim(
            "roles",
            userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
        .sign(algorithm);
  }

  @Override
  public Customer getCustomerFromAuthorizationHeader(String bearerToken) {
    Dotenv dotenv = Dotenv.load();
    String token = bearerToken.substring("bearer ".length());
    Algorithm algorithm =
        Algorithm.HMAC512(
            Objects.requireNonNull(dotenv.get("JWT_SECRET_KEY")).getBytes(StandardCharsets.UTF_8));
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT decodedJWT = verifier.verify(token);
    String username = decodedJWT.getSubject();
    Customer customer = getCustomerByUsername(username);
    if (customer == null) {
      throw new NoSuchCustomerException("Customer service: getCustomerFromAuthorizationHeader()");
    }
    return customer;
  }
}
