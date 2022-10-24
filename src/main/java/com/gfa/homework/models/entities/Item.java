package com.gfa.homework.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gfa.homework.models.dtos.ResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Item implements ResponseDto {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private String name;
  private String description;
  private String photoUrl;
  private String seller;
  private int startingPrice;
  private int lastBid;
  private int purchasePrice;
  @OneToOne private Customer buyer;
  @JsonIgnore @OneToMany private List<Bid> bids;

  public Item(
      String name,
      String description,
      String photoUrl,
      String seller,
      int startingPrice,
      int purchasePrice) {
    this.name = name;
    this.description = description;
    this.photoUrl = photoUrl;
    this.seller = seller;
    this.startingPrice = startingPrice;
    this.lastBid = 0;
    this.purchasePrice = purchasePrice;
    this.bids = new ArrayList<>();
  }
}
