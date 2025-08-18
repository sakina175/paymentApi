package com.paymentapi.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransactionRequest {
    @NotEmpty
    private String cardNumber;
    
    @NotEmpty
    @Pattern(regexp = "\\d{2}/\\d{2}")
    private String expiryDate;
    
    @NotEmpty
    @Size(min = 3, max = 4)
    private String cvv;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotEmpty
    private String currency;
    
}
