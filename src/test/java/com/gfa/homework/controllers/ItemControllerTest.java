package com.gfa.homework.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gfa.homework.exceptions.BidTooLowException;
import com.gfa.homework.exceptions.InsufficientFundsException;
import com.gfa.homework.exceptions.InvalidUrlException;
import com.gfa.homework.models.dtos.NewItemDto;
import com.gfa.homework.models.dtos.UsernamePasswordDto;
import com.gfa.homework.repositories.CustomerRepository;
import com.gfa.homework.repositories.ItemRepository;
import com.gfa.homework.services.CustomerService;
import com.gfa.homework.services.ItemService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ItemControllerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired private MockMvc mockMvc;
  @Autowired private CustomerService customerService;
  @Autowired private ItemService itemService;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private ItemRepository itemRepository;

  @BeforeEach
  void init() {
    customerRepository.deleteAll();
    itemRepository.deleteAll();
  }

  @Test
  @Order(1)
  void returns_ok_if_adds_new_item() throws Exception {
    customerService.saveNewCustomer(new UsernamePasswordDto("user", "password"));
    NewItemDto dto = new NewItemDto("Item", "Description", "https://www.images.com", 20, 30);
    mockMvc
        .perform(
            post("/api/v1/items/add")
                .header(
                    "Authorization",
                    "Bearer "
                        + customerService.getToken(
                            customerService.getUserDetailsByUsername("user")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", Is.is("Item")))
        .andDo(print());
  }

  @Test
  @Order(2)
  void throws_exception_if_photo_url_not_valid() throws Exception {
    customerService.saveNewCustomer(new UsernamePasswordDto("user", "password"));
    NewItemDto dto = new NewItemDto("Item", "Description", "ww.images.com", 20, 30);
    mockMvc
        .perform(
            post("/api/v1/items/add")
                .header(
                    "Authorization",
                    "Bearer "
                        + customerService.getToken(
                            customerService.getUserDetailsByUsername("user")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotAcceptable())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof InvalidUrlException))
        .andDo(print());
  }

  @Test
  @Order(3)
  void can_show_all_sellable_items() throws Exception {
    customerService.saveNewCustomer(new UsernamePasswordDto("user", "password"));
    NewItemDto dto = new NewItemDto("Item", "Description", "www.images.com", 20, 30);
    itemService.saveNewItem(dto, "user");
    mockMvc
        .perform(
            get("/api/v1/items/sellable")
                .header(
                    "Authorization",
                    "Bearer "
                        + customerService.getToken(
                            customerService.getUserDetailsByUsername("user")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sellableItems", hasSize(1)))
        .andDo(print());
  }

  @Test
  @Order(4)
  void can_show_specific_item_by_id() throws Exception {
    customerService.saveNewCustomer(new UsernamePasswordDto("user", "password"));
    NewItemDto dto = new NewItemDto("Item", "Description", "www.images.com", 20, 30);
    itemService.saveNewItem(dto, "user");
    mockMvc
        .perform(
            get("/api/v1/items/21")
                .header(
                    "Authorization",
                    "Bearer "
                        + customerService.getToken(
                            customerService.getUserDetailsByUsername("user")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", Is.is("Item")))
        .andDo(print());
  }

  @Test
  @Order(5)
  void returns_bad_request_and_exception_if_not_enough_money() throws Exception {
    customerService.saveNewCustomer(new UsernamePasswordDto("user", "password"));
    NewItemDto dto = new NewItemDto("Item", "Description", "www.images.com", 20, 30);
    itemService.saveNewItem(dto, "user");
    mockMvc
        .perform(
            post("/api/v1/items/19/1000")
                .header(
                    "Authorization",
                    "Bearer "
                        + customerService.getToken(
                            customerService.getUserDetailsByUsername("user")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(result.getResolvedException() instanceof InsufficientFundsException))
        .andDo(print());
  }

  @Test
  @Order(6)
  void returns_bad_request_and_exception_if_bid_too_low() throws Exception {
    customerService.saveNewCustomer(new UsernamePasswordDto("user", "password"));
    NewItemDto dto = new NewItemDto("Item", "Description", "www.images.com", 20, 30);
    itemService.saveNewItem(dto, "user");
    mockMvc
        .perform(
            post("/api/v1/items/22/10")
                .header(
                    "Authorization",
                    "Bearer "
                        + customerService.getToken(
                            customerService.getUserDetailsByUsername("user")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotAcceptable())
        .andExpect(
            result -> assertTrue(result.getResolvedException() instanceof BidTooLowException))
        .andDo(print());
  }
}
