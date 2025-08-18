package com.paymentapi.model;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String stripeChargeId; // Stripe's transaction ID
    private BigDecimal amount;
    private String currency;
    private String status; // "succeeded", "failed"
    private String last4Digits; // Last 4 digits of card
    
    // No-args constructor for JPA
    public PaymentTransaction() {}
}