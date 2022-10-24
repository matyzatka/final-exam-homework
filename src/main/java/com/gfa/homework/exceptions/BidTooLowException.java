package com.gfa.homework.exceptions;

public class BidTooLowException extends RuntimeException {
  public BidTooLowException(String message) {
    super(message);
  }
}
