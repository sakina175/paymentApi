package com.paymentapi.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private long paymentId;
    private String status;
    private Instant timestamp;
}