package com.gfa.homework.exceptions;

public class NoSuchCustomerException extends RuntimeException {
  public NoSuchCustomerException(String message) {
    super(message);
  }
}
