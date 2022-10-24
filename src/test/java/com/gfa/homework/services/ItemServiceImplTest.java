package com.gfa.homework.services;

import com.gfa.homework.models.dtos.NewItemDto;
import com.gfa.homework.models.entities.Customer;
import com.gfa.homework.models.entities.Item;
import com.gfa.homework.repositories.BidRepository;
import com.gfa.homework.repositories.CustomerRepository;
import com.gfa.homework.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceImplTest {

  @Autowired private ModelMapper modelMapper;
  private ItemService itemService;
  private ItemRepository itemRepository;

  @BeforeEach
  void init() {
    itemRepository = mock(ItemRepository.class);
    BidRepository bidRepository = mock(BidRepository.class);
    CustomerRepository customerRepository = mock(CustomerRepository.class);
    itemService =
        new ItemServiceImpl(modelMapper, itemRepository, bidRepository, customerRepository);
  }

  @Test
  void true_if_photo_has_valid_url() {
    assertTrue(
        itemService.itemHasValidPhotoUrl(
            new NewItemDto("name", "desc", "https://www.google.com", 1, 2)));
  }

  @Test
  void can_save_new_item() {
    NewItemDto dto = new NewItemDto("name", "desc", "https://www.google.com", 1, 2);
    itemService.saveNewItem(dto, "username");
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void can_convert_to_specific_sellable_dto() {
    assertNotNull(
        itemService.convertToSpecificSellableDto(
            new Item("name", "desc", "https://www.google.com", "username", 1, 2)));
  }

  @Test
  void can_convert_to_specific_not_sellable_dto() {
    assertNotNull(
        itemService.convertToSpecificNotSellableDto(
            new Item("name", "desc", "https://www.google.com", "username", 1, 2)));
  }

  @Test
  void can_convert_to_sellable_dto() {
    assertNotNull(
        itemService.convertToSellableDto(
            new Item("name", "desc", "https://www.google.com", "username", 1, 2)));
  }

  @Test
  void can_get_only_sellable_items() {
    when(itemService.getOnlySellableItems()).thenReturn(List.of(new Item(), new Item()));
    assertFalse(itemService.getOnlySellableItems().isEmpty());
  }

  @Test
  void can_convert_all_sellable_items_to_dto() {
    when(itemService.getOnlySellableItems()).thenReturn(List.of(new Item(), new Item()));
    assertFalse(
        itemService.convertAllSellableItemsToDto(itemService.getOnlySellableItems()).isEmpty());
  }

  @Test
  void can_get_all_sellable_items_as_dto() {
    when(itemService.getOnlySellableItems()).thenReturn(List.of(new Item(), new Item()));
    assertNotNull(
        itemService.getAllSellableItemsAsDto(
            itemService.convertAllSellableItemsToDto(itemService.getOnlySellableItems())));
  }

  @Test
  void can_get_items_by_id() {
    when(itemRepository.findById(1L)).thenReturn(Optional.of(new Item()));
    assertNotNull(itemService.getItemById(1L));
  }

  @Test
  void returns_true_if_item_has_buyer() {
    Item item = new Item();
    item.setBuyer(new Customer());
    assertTrue(itemService.hasBuyer(item));
  }

  @Test
  void can_save_item() {
    NewItemDto dto = new NewItemDto("name", "desc", "https://www.google.com", 1, 2);
    itemService.saveNewItem(dto, "username");
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void returns_true_if_can_place_a_bid() {
    Customer customer = new Customer();
    customer.setBalance(100);
    Item item = new Item();
    item.setLastBid(0);
    item.setStartingPrice(10);
    item.setPurchasePrice(50);
    assertTrue(itemService.canPlaceBid(customer, 40, item));
  }
}
