package com.gfa.homework.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BearerTokenDto implements ResponseDto {

  private String accessToken;
}
