package com.paymentapi.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.paymentapi.dto.PaymentRequest;
import com.paymentapi.dto.PaymentResponse;
import com.paymentapi.dto.TransactionRequest;
import com.paymentapi.model.PaymentTransaction;
import com.paymentapi.model.Payment;
import com.paymentapi.repositry.ChargeRepositry;
import com.paymentapi.repositry.PaymentRepositry;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Service
@RequiredArgsConstructor
public class PaymentServices {
    private final PaymentRepositry repo;
    private final ChargeRepositry chargeRepositry;
    
    @org.springframework.beans.factory.annotation.Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }
    
    public PaymentResponse createPayment(PaymentRequest paymentRequest){
        Payment payment=new Payment();
        payment.setAmount(paymentRequest.getAmount());
        payment.setReceiverAccount(paymentRequest.getReceiverAccount());
        payment.setSenderAccount(paymentRequest.getReceiverAccount());

        Payment savedPayment = repo.save(payment);
        
        return new PaymentResponse(
            savedPayment.getId(),
            "ok",
            savedPayment.getTimestamp()
        );
    }

    public Map<String,Object> createPaymentIntent(@RequestBody Map<String,Object> requestData) throws StripeException{
        Map<String,Object> params=new HashMap<>();
        //from dollar to cent
        Double amountInDouble = Double.parseDouble(requestData.get("amount").toString());
        Long amountInCents=Math.round(amountInDouble*100);
        params.put("amount", amountInCents);
        params.put("currency", "usd");

        PaymentIntent intent=PaymentIntent.create(params);
        Map<String,Object> response=new HashMap<>();
        response.put("clientSecret", intent.getClientSecret());

        return response;
    }
}
// public PaymentTransaction createCharge(String token, BigDecimal amount, String currency) throws StripeException {
    //     Map<String, Object> chargeParams = new HashMap<>();
    //     chargeParams.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
    //     chargeParams.put("currency", currency);
    //     chargeParams.put("source", token); // Token from frontend
        
    //     PaymentTransaction stripeCharge = PaymentTransaction.create(chargeParams);
        
    //     // Convert to our entity
    //     PaymentTransaction charge = new PaymentTransaction();
    //     charge.setStripeChargeId(stripeCharge.getId());
    //     charge.setAmount(amount);
    //     charge.setCurrency(currency);
    //     charge.setStatus(stripeCharge.getStatus());
    //     charge.setLast4Digits(stripeCharge.getPaymentMethodDetails().getCard().getLast4());
        
    //     return chargeRepositry.save(charge);
    // }
