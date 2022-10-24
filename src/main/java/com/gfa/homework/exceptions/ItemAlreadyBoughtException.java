package com.gfa.homework.exceptions;

public class ItemAlreadyBoughtException extends RuntimeException {
  public ItemAlreadyBoughtException(String message) {
    super(message);
  }
}
