package com.paymentapi.repositry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymentapi.model.PaymentTransaction;

public interface ChargeRepositry extends JpaRepository<PaymentTransaction,Long>{
    
}
