package com.gfa.homework.controllers;

import com.gfa.homework.exceptions.InvalidInputException;
import com.gfa.homework.exceptions.InvalidUrlException;
import com.gfa.homework.models.dtos.NewItemDto;
import com.gfa.homework.models.dtos.ResponseDto;
import com.gfa.homework.models.entities.Customer;
import com.gfa.homework.models.entities.Item;
import com.gfa.homework.services.CustomerService;
import com.gfa.homework.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

  private final ItemService itemService;
  private final CustomerService customerService;

  @PostMapping("/add")
  public ResponseEntity<ResponseDto> addNewItem(
      @RequestHeader(name = "Authorization") String bearerToken,
      @RequestBody @Valid NewItemDto dto) {
    Customer customer = customerService.getCustomerFromAuthorizationHeader(bearerToken);
    if (!itemService.itemHasValidPhotoUrl(dto)) {
      throw new InvalidUrlException("/api/v1/items/add");
    }
    return ok(itemService.saveNewItem(dto, customer.getUsername()));
  }

  @GetMapping("/sellable")
  public ResponseEntity<ResponseDto> showAllSellableItems() {
    return ok(
        itemService.getAllSellableItemsAsDto(
            itemService.convertAllSellableItemsToDto(itemService.getOnlySellableItems())));
  }

  @GetMapping("/sellable/{page}")
  public ResponseEntity<ResponseDto> showAllSellableItems(@PathVariable Integer page) {
    return ok(
        itemService.getAllSellableItemsAsDto(
            itemService.convertAllSellableItemsToDto(itemService.getOnlySellableItems(page))));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseDto> getItemById(@PathVariable Long id) {
    if (id < 1) {
      throw new InvalidInputException("/api/v1/items/" + id);
    }
    Item item = itemService.getItemById(id);
    if (itemService.hasBuyer(item)) {
      return ok(itemService.convertToSpecificNotSellableDto(item));
    }
    return ok(itemService.convertToSpecificSellableDto(item));
  }

  @PostMapping("/{id}/{bid}")
  public ResponseEntity<ResponseDto> placeBidOnItem(
      @RequestHeader(name = "Authorization") String bearerToken,
      @PathVariable(name = "id") Long id,
      @PathVariable(name = "bid") Integer bid) {
    if (id < 1) {
      throw new InvalidInputException("/api/v1/items/" + id);
    }
    Customer customer = customerService.getCustomerFromAuthorizationHeader(bearerToken);
    Item item = itemService.getItemById(id);
    if (itemService.canPlaceBid(customer, bid, item)) {
      return ok(itemService.placeBidOrBuy(customer, bid, item));
    }
    return badRequest().build();
  }
}
