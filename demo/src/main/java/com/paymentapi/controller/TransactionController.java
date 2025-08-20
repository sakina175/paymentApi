package com.paymentapi.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymentapi.service.TransactionServices;
import com.stripe.exception.StripeException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionServices transactionServices;

    @PostMapping("/create-payment-intent")
    public Map<String,Object> createPaymentIntent(@RequestBody Map<String,Object> requestData) throws StripeException{
        Map<String,Object> response= transactionServices.createPaymentIntent(requestData);
        return response;
    }
    @PostMapping("/update-payment-status")
    public Map<String,Object> updatePaymentStatus(@RequestBody Map<String,Object> requestData) {
        Map<String,Object> response= transactionServices.updatePaymentStatus(requestData);
        return response;
    }
}