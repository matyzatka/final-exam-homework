package com.gfa.homework.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfa.homework.exceptions.CustomerAlreadyExistsException;
import com.gfa.homework.exceptions.IncorrectPasswordException;
import com.gfa.homework.exceptions.NoSuchCustomerException;
import com.gfa.homework.models.dtos.UsernamePasswordDto;
import com.gfa.homework.repositories.CustomerRepository;
import com.gfa.homework.services.CustomerService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private CustomerService customerService;
  @Autowired private CustomerRepository customerRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void init() {
    customerRepository.deleteAll();
  }

  @Test
  void returns_ok_if_signs_up_customer() throws Exception {
    String username = "new_username";
    String password = "new_password";
    UsernamePasswordDto dto = new UsernamePasswordDto(username, password);
    mockMvc
        .perform(
            post("/api/v1/customers/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(new UsernamePasswordDto(username, password))))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  void throws_exception_if_user_exists() throws Exception {
    String username = "new_username";
    String password = "new_password";
    UsernamePasswordDto dto = new UsernamePasswordDto(username, password);
    customerService.saveNewCustomer(dto);
    mockMvc
        .perform(
            post("/api/v1/customers/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(new UsernamePasswordDto(username, password))))
        .andExpect(
            result ->
                assertTrue(result.getResolvedException() instanceof CustomerAlreadyExistsException))
        .andDo(print());
  }

  @Test
  void throws_exception_if_user_not_exists_when_login() throws Exception {
    String username = "new_username";
    String password = "new_password";
    UsernamePasswordDto dto = new UsernamePasswordDto(username, password);
    mockMvc
        .perform(
            post("/api/v1/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(new UsernamePasswordDto(username, password))))
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof NoSuchCustomerException))
        .andDo(print());
  }

  @Test
  void throws_exception_if_password_not_correct() throws Exception {
    String username = "new_username";
    String password = "new_password";
    UsernamePasswordDto dto = new UsernamePasswordDto(username, password);
    customerService.saveNewCustomer(dto);
    mockMvc
        .perform(
            post("/api/v1/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new UsernamePasswordDto(username, "wrong_password"))))
        .andExpect(
            result ->
                assertTrue(result.getResolvedException() instanceof IncorrectPasswordException))
        .andDo(print());
  }

  @Test
  void if_login_successful_returns_ok_and_jwt() throws Exception {
    String username = "new_username";
    String password = "new_password";
    UsernamePasswordDto dto = new UsernamePasswordDto(username, password);
    customerService.saveNewCustomer(dto);
    mockMvc
        .perform(
            post("/api/v1/customers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString())
        .andDo(print());
  }
}
