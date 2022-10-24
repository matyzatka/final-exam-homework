package com.gfa.homework.advice;

import com.gfa.homework.exceptions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CustomExceptionHandlerTest {

  @Autowired private CustomExceptionHandler customExceptionHandler;

  @Test
  void handleCustomerAlreadyExistsException() {
    assertNotNull(
        customExceptionHandler.handleCustomerAlreadyExistsException(
            new CustomerAlreadyExistsException("/path")));
  }

  @Test
  void handleIncorrectPasswordException() {
    assertNotNull(
        customExceptionHandler.handleIncorrectPasswordException(
            new IncorrectPasswordException("/path")));
  }

  @Test
  void handleAccessDeniedException() {
    assertNotNull(
        customExceptionHandler.handleAccessDeniedException(new AccessDeniedException("/path")));
  }

  @Test
  void handleNoSuchCustomerException() {
    assertNotNull(
        customExceptionHandler.handleNoSuchCustomerException(new NoSuchCustomerException("/path")));
  }

  @Test
  void handleInvalidUrlException() {
    assertNotNull(
        customExceptionHandler.handleInvalidUrlException(new InvalidUrlException("/path")));
  }

  @Test
  void handleNoSuchItemException() {
    assertNotNull(
        customExceptionHandler.handleNoSuchItemException(new NoSuchItemException("/path")));
  }

  @Test
  void handleInvalidInputException() {
    assertNotNull(
        customExceptionHandler.handleInvalidInputException(new InvalidInputException("/path")));
  }

  @Test
  void handleNumberFormatException() {
    assertNotNull(
        customExceptionHandler.handleNumberFormatException(new NumberFormatException("/path")));
  }

  @Test
  void handleIllegalArgumentException() {
    assertNotNull(
        customExceptionHandler.handleIllegalArgumentException(
            new IllegalArgumentException("/path")));
  }

  @Test
  void handleItemAlreadyBoughtException() {
    assertNotNull(
        customExceptionHandler.handleItemAlreadyBoughtException(
            new ItemAlreadyBoughtException("/path")));
  }

  @Test
  void handleBidTooLowException() {
    assertNotNull(customExceptionHandler.handleBidTooLowException(new BidTooLowException("/path")));
  }

  @Test
  void handleInsufficientFundsException() {
    assertNotNull(
        customExceptionHandler.handleInsufficientFundsException(
            new InsufficientFundsException("/path")));
  }
}
