package com.gfa.homework.models.dtos;

import com.gfa.homework.models.entities.Item;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDto implements ResponseDto {

  private String username;
  private List<Item> itemsForSale;
  private List<Item> itemsOwned;
  private int balance;
}
