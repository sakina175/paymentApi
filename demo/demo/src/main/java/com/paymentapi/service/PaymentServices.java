package com.paymentapi.service;

import org.springframework.stereotype.Service;

import com.paymentapi.dto.PaymentRequest;
import com.paymentapi.dto.PaymentResponse;
import com.paymentapi.model.Payment;
import com.paymentapi.repositry.PaymentRepositry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServices {
    private final PaymentRepositry repo;

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
}