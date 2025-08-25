package com.paymentapi.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.paymentapi.model.Customers;
import com.paymentapi.model.Transactions;
import com.paymentapi.repositry.CustomerRepositry;
import com.paymentapi.repositry.TransactionRepositry;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServices {
    private final TransactionRepositry transactionRepositry;
    private final CustomerRepositry customerRepositry;
    
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }
    
    public Map<String,Object> createPaymentIntent(@RequestBody Map<String,Object> requestData) throws StripeException{
        //find respective customer
        Long customerId = ((Number) requestData.get("id")).longValue();
        Customers customers=customerRepositry.findById(customerId)
        .orElseThrow(()-> new RuntimeException("Customer not Found"));

        //from dollar to cent
        Map<String,Object> params=new HashMap<>();
        Double amountInDouble = Double.parseDouble(requestData.get("amount").toString());
        Long amountInCents=Math.round(amountInDouble*100);
        params.put("amount", amountInCents);
        params.put("currency", "usd");
        params.put("customer", requestData.get("customerStripeId"));

        PaymentIntent intent=PaymentIntent.create(params);
        
        Transactions newTransaction= new Transactions();
        newTransaction.setAmount(BigDecimal.valueOf(amountInDouble));
        newTransaction.setCurrency(params.get("currency").toString());
        newTransaction.setStripePaymentIntentId(intent.getClientSecret().toString());
        newTransaction.setStatus(intent.getStatus().toUpperCase());
        newTransaction.setCustomer(customers);
        newTransaction.setFailureReason(intent.getCancellationReason());
        transactionRepositry.save(newTransaction);
        
        Map<String,Object> response=new HashMap<>();
        response.put("clientSecret", intent.getClientSecret());
        return response;
    }

    public Map<String, Object> updatePaymentStatus(Map<String,Object> requestData) {
        Transactions localTransaction=transactionRepositry.findByStripePaymentIntentId(requestData.get("paymentIntent").toString());
        // .orElseThrow(()->new RuntimeException("Invalid Transaction ID"));
        localTransaction.setStatus(requestData.get("status").toString());
        transactionRepositry.save(localTransaction);

        Map<String, Object> response= new HashMap<>();
        response.put("transactionId", localTransaction.getId());
        response.put("status", localTransaction.getStatus());

        return response;
    }
}