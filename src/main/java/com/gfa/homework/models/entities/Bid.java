package com.gfa.homework.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Bid {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @ManyToOne @JsonIgnore private Item item;
  @OneToOne private Customer customer;
  private int amount;
  private String datetime;

  public Bid(Item item, Customer customer, int amount) {
    this.item = item;
    this.customer = customer;
    this.amount = amount;
    LocalDateTime timeNow = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    this.datetime = timeNow.format(formatter);
  }
}
