package com.paymentapi.repositry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymentapi.model.Payment;

public interface PaymentRepositry extends JpaRepository<Payment,Long>{}