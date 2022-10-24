package com.gfa.homework.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class NewItemDto {

  @NotBlank @NotNull private String name;
  @NotBlank @NotNull private String description;
  @NotBlank @NotNull private String photoUrl;

  @NotNull
  @Min(1)
  private Integer startingPrice;

  @NotNull
  @Min(1)
  private Integer purchasePrice;
}
