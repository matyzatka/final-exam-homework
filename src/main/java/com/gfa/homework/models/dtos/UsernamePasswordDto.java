package com.gfa.homework.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UsernamePasswordDto implements ResponseDto {

  @NotNull @NotBlank private String username;
  @NotNull @NotBlank private String password;
}
