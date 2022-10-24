package com.gfa.homework.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllSellableDto implements ResponseDto {

  private List<SellableDto> sellableItems;
}
