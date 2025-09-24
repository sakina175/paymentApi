package com.paymentapi.service;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentapi.model.Customers;
import com.paymentapi.model.PendingEvent;
import com.paymentapi.model.Transactions;
import com.paymentapi.repositry.PendingEventRepositry;
import com.paymentapi.repositry.TransactionRepositry;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeWebhookServices {
    private final TransactionRepositry transactionRepositry;
    private final PendingEventRepositry pendingEventRepositry;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    public StripeWebhookServices(TransactionRepositry transactionRepositry,
            PendingEventRepositry pendingEventRepositry) {
        this.transactionRepositry = transactionRepositry;
        this.pendingEventRepositry = pendingEventRepositry;
    }

    public String handleStripeEvent(HttpServletRequest request, @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws IOException {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            System.out.println("signature error:" + e);
            return "Invalid Signature";
        }
        //event is globally unique , avoid duplication from db and notify hits
        if (pendingEventRepositry.existsByEventId(event.getId())) {
            System.out.println("duplicate event");
            return "duplicate event";
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);
            JsonNode dataObject = root.path("data").path("object");

            String paymentIntentId = dataObject.has("id") ? dataObject.get("id").asText() : null;
            if (paymentIntentId == null) {
                System.out.println("Payment intent id missing in payload");
                return "Missing Payment Intent Id";
            }

            Transactions localTransactions = transactionRepositry.findByStripePaymentIntentId(paymentIntentId);
            if (localTransactions == null) {
                System.out.println("No matching local transaction for PaymentIntent ID: " + paymentIntentId);
                return "Transaction not found";
            }
            Customers customer = localTransactions.getCustomer();
            
            String eventType = event.getType();
            localTransactions.setStatus(eventType);

            // pending obj value setting
            PendingEvent pendingEvent = new PendingEvent();
            pendingEvent.setEventId(event.getId());
            pendingEvent.setPaymentIntentId(paymentIntentId);
            pendingEvent.setCreatedAt(Instant.now());
            pendingEvent.setStatus(eventType);
            pendingEvent.setEmail(customer.getEmail());//use from localTransactions data
            pendingEventRepositry.save(pendingEvent);

            if ("payment_intent.payment_failed".equals(eventType) && dataObject.has("last_payment_error")) {
                JsonNode errorNode = dataObject.get("last_payment_error");
                localTransactions.setFailureReason(errorNode.has("code") ? errorNode.get("code").asText() : "UNKNOWN");
            }

            transactionRepositry.save(localTransactions);

            System.out.println("udpate tran through hook successfully");
            return "Ok";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing webhook";
        }
    }
}
