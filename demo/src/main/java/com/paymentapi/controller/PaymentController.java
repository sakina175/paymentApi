package com.paymentapi.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymentapi.dto.PaymentRequest;
import com.paymentapi.dto.PaymentResponse;
import com.paymentapi.model.PaymentTransaction;
import com.paymentapi.service.PaymentServices;
import com.stripe.exception.StripeException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentServices paymentServices;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
        @Valid @RequestBody PaymentRequest request
    ){
        PaymentResponse response=paymentServices.createPayment(request);
        return ResponseEntity.ok(response);
    }

    // @PostMapping("/card/proceed")
    // public PaymentTransaction proceedCardPayment(
    //     @RequestParam String token,
    //     @RequestParam BigDecimal amount,
    //     @RequestParam String currency
    // )throws StripeException{
    //     PaymentTransaction response= paymentServices.createCharge(token,amount,currency);
    //     return response;
    // }

    @PostMapping("/create-payment-intent")
    public Map<String,Object> createPaymentIntent(@RequestBody Map<String,Object> requestData) throws StripeException{
        Map<String,Object> response= paymentServices.createPaymentIntent(requestData);
        return response;
    }
}