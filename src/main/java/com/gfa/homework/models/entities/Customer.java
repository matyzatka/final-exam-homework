package com.gfa.homework.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Customer {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @JsonIgnore
  private Long id;

  private String username;
  @JsonIgnore private String password;
  @JsonIgnore private int balance;

  @JsonIgnore @OneToMany private List<Item> itemsForSale;
  @JsonIgnore @OneToMany private List<Item> itemsOwned;

  public Customer(String username, String password) {
    this.username = username;
    this.password = password;
    this.itemsForSale = new ArrayList<>();
    this.itemsOwned = new ArrayList<>();
    this.balance = 100;
  }
}
