package com.gfa.homework.services;

import com.gfa.homework.exceptions.BidTooLowException;
import com.gfa.homework.exceptions.InsufficientFundsException;
import com.gfa.homework.exceptions.ItemAlreadyBoughtException;
import com.gfa.homework.exceptions.NoSuchItemException;
import com.gfa.homework.models.dtos.*;
import com.gfa.homework.models.entities.Bid;
import com.gfa.homework.models.entities.Customer;
import com.gfa.homework.models.entities.Item;
import com.gfa.homework.repositories.BidRepository;
import com.gfa.homework.repositories.CustomerRepository;
import com.gfa.homework.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

  private final ModelMapper modelMapper;
  private final ItemRepository itemRepository;
  private final BidRepository bidRepository;
  private final CustomerRepository customerRepository;

  @Override
  public boolean itemHasValidPhotoUrl(NewItemDto dto) {
    UrlValidator validator = new UrlValidator();
    return validator.isValid(dto.getPhotoUrl());
  }

  @Override
  public Item saveNewItem(NewItemDto dto, String username) {
    return itemRepository.save(
        new Item(
            dto.getName(),
            dto.getDescription(),
            dto.getPhotoUrl(),
            username,
            dto.getStartingPrice(),
            dto.getPurchasePrice()));
  }

  @Override
  public SpecificSellableDto convertToSpecificSellableDto(Item item) {
    return modelMapper.map(item, SpecificSellableDto.class);
  }

  @Override
  public SpecificNotSellableDto convertToSpecificNotSellableDto(Item item) {
    return modelMapper.map(item, SpecificNotSellableDto.class);
  }

  @Override
  public SellableDto convertToSellableDto(Item item) {
    return modelMapper.map(item, SellableDto.class);
  }

  @Override
  public List<Item> getOnlySellableItems() {
    return itemRepository.findAll().stream()
        .filter(item -> item.getBuyer() == null)
        .limit(20)
        .collect(Collectors.toList());
  }

  @Override
  public List<Item> getOnlySellableItems(int n) {
    return itemRepository.findAll().stream()
        .filter(item -> item.getBuyer() == null)
        .skip((n - 1) * 20L)
        .limit(20)
        .collect(Collectors.toList());
  }

  @Override
  public List<SellableDto> convertAllSellableItemsToDto(List<Item> sellableItems) {
    return sellableItems.stream().map(this::convertToSellableDto).collect(Collectors.toList());
  }

  @Override
  public AllSellableDto getAllSellableItemsAsDto(List<SellableDto> sellableDtos) {
    return new AllSellableDto(sellableDtos);
  }

  @Override
  public Item getItemById(Long id) {
    Optional<Item> item = itemRepository.findById(id);
    if (!item.isPresent()) {
      throw new NoSuchItemException("Item service: getSellableDtoById()");
    }
    return item.get();
  }

  @Override
  public boolean hasBuyer(Item item) {
    return item.getBuyer() != null;
  }

  @Override
  public void save(Item item) {
    itemRepository.save(item);
  }

  @Override
  public boolean canPlaceBid(Customer customer, int bid, Item item) {
    if (hasBuyer(item)) {
      throw new ItemAlreadyBoughtException("Item service: canPlaceBid()");
    }
    if (customer.getBalance() < bid) {
      throw new InsufficientFundsException("Item service: canPlaceBid()");
    }
    if (bid <= item.getLastBid() || bid <= item.getStartingPrice()) {
      throw new BidTooLowException("Item service: canPlaceBid()");
    }
    return true;
  }

  @Override
  public ResponseDto placeBidOrBuy(Customer customer, int bid, Item item) {
    if (bid < item.getPurchasePrice()) {
      Bid newBid = new Bid(item, customer, bid);
      newBid.setCustomer(customer);
      newBid.setItem(item);
      bidRepository.save(newBid);
      item.getBids().add(newBid);
      item.setLastBid(bid);
      itemRepository.save(item);
      customer.setBalance(customer.getBalance() - bid);
      customerRepository.save(customer);
      return convertToSpecificSellableDto(item);
    } else {
      item.getBids() // returns money from placed bids back to customers account
          .forEach(
              previousBid -> {
                Customer bidder = previousBid.getCustomer();
                bidder.setBalance(bidder.getBalance() + previousBid.getAmount());
                customerRepository.save(bidder);
              });
      item.setBuyer(customer);
      itemRepository.save(item);
      customer.getItemsOwned().add(item);
      customer.setBalance(customer.getBalance() - item.getPurchasePrice());
      customerRepository.save(customer);
      return convertToSpecificNotSellableDto(item);
    }
  }
}
