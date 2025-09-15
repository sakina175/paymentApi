package com.paymentapi.repositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paymentapi.model.Transactions;

@Repository
public interface TransactionRepositry extends JpaRepository<Transactions,Long>{

    Transactions findByStripePaymentIntentId(String stripePaymentIntentId);}