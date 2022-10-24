package com.gfa.homework.security;

import com.gfa.homework.models.dtos.UsernamePasswordDto;
import com.gfa.homework.repositories.CustomerRepository;
import com.gfa.homework.security.SecurityConfiguration.CustomAuthorizationFilter;
import com.gfa.homework.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CustomAuthorizationFilterTest {

  @Autowired CustomerService customerService;
  @Autowired CustomerRepository customerRepository;

  @Test
  public void custom_filter_test() throws ServletException, IOException {
    customerRepository.deleteAll();
    customerService.saveNewCustomer(new UsernamePasswordDto("user", "password"));
    String bearerToken =
        "Bearer " + customerService.getToken(customerService.getUserDetailsByUsername("user"));

    HttpServletRequest servletRequest = mock(HttpServletRequest.class);
    HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    CustomAuthorizationFilter customAuthorizationFilter = new CustomAuthorizationFilter();
    assertThrows(
        Exception.class,
        () -> customAuthorizationFilter.doFilter(servletRequest, servletResponse, filterChain));

    when(servletRequest.getHeader(AUTHORIZATION)).thenReturn(bearerToken);
    customAuthorizationFilter.doFilter(servletRequest, servletResponse, filterChain);
    verify(filterChain).doFilter(servletRequest, servletResponse);
  }
}
