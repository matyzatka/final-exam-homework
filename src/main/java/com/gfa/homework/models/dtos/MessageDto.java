package com.gfa.homework.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDto implements ResponseDto {

  private String message;
}
