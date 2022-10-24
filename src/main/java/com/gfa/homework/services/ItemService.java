package com.gfa.homework.services;

import com.gfa.homework.models.dtos.*;
import com.gfa.homework.models.entities.Customer;
import com.gfa.homework.models.entities.Item;

import java.util.List;

public interface ItemService {

  boolean itemHasValidPhotoUrl(NewItemDto dto);

  Item saveNewItem(NewItemDto dto, String username);

  SpecificSellableDto convertToSpecificSellableDto(Item item);

  SpecificNotSellableDto convertToSpecificNotSellableDto(Item item);

  SellableDto convertToSellableDto(Item item);

  AllSellableDto getAllSellableItemsAsDto(List<SellableDto> sellableDtos);

  List<Item> getOnlySellableItems();

  List<Item> getOnlySellableItems(int n);

  List<SellableDto> convertAllSellableItemsToDto(List<Item> sellables);

  Item getItemById(Long id);

  boolean hasBuyer(Item item);

  void save(Item item);

  boolean canPlaceBid(Customer customer, int amount, Item item);

  ResponseDto placeBidOrBuy(Customer customer, int bid, Item item);
}
