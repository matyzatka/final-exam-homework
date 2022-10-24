package com.gfa.homework.repositories;

import com.gfa.homework.models.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
  boolean existsCustomerByUsername(String username);

  Customer getCustomerByUsername(String username);
}
