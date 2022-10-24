package com.gfa.homework.models.dtos;

import lombok.Data;

@Data
public class SellableDto implements ResponseDto {

  private String name;
  private String photoUrl;
  private int lastBid;
}
