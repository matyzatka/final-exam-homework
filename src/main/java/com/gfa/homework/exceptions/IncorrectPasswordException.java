package com.gfa.homework.exceptions;

public class IncorrectPasswordException extends RuntimeException {

  public IncorrectPasswordException(String message) {
    super(message);
  }
}
