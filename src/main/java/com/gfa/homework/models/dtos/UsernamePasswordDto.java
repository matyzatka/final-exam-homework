package com.gfa.homework.models.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UsernamePasswordDto implements ResponseDto {

  @NotNull @NotBlank private String username;
  @NotNull @NotBlank private String password;
}
