package com.paymentapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor
public class Customers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "stripe_customer_id", unique = true, length = 50)
    private String stripeCustomerId;  // Changed to camelCase

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Transactions> transactions = new ArrayList<>();

    // Business methods
    public void addTransaction(Transactions transaction) {
        transactions.add(transaction);
        transaction.setCustomer(this);
    }
}