package com.paymentapi.dto;
import java.math.BigDecimal;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {
    @NonNull @Positive
    private BigDecimal amount;

    @NotBlank
    private String senderAccount;

    @NotBlank
    private String receiverAccount;
    
}