package com.paymentapi.repositry;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymentapi.model.Customers;

@Repository
public interface CustomerRepositry extends JpaRepository<Customers,Long>{

    Optional<Customers> findByStripeCustomerId(String stripeCustomerId);

    Optional<Customers> findByEmail(String custEmail);
    
}
