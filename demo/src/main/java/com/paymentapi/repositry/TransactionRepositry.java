package com.paymentapi.repositry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymentapi.model.Transactions;

public interface TransactionRepositry extends JpaRepository<Transactions,Long>{

    Transactions findByStripePaymentIntentId(String stripePaymentIntentId);}