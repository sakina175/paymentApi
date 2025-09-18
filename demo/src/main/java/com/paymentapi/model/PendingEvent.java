package com.paymentapi.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pending_events", uniqueConstraints = {
    @UniqueConstraint(columnNames = "event_id") // ensures each Stripe event.id is unique
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, length = 100, unique = true)
    private String eventId; // Stripe event.id

    @Column(name = "payment_intent_id", nullable = false, length = 100)
    private String paymentIntentId; // Stripe PaymentIntent ID

    @Column(nullable = false, length = 100)
    private String status; // e.g., payment_intent.succeeded

    @Column(nullable = false, length = 150)
    private String email; // customer email

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt; // when event was received

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
