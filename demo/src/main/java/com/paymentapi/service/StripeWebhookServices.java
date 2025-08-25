package com.paymentapi.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;

public class StripeWebhookServices {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    public String handleStripeEvent(HttpServletRequest request, @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws IOException {
        Event event;
        try {
            // verify webhook signature
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

        } catch (SignatureVerificationException exception) {
            System.out.println("signature verification failed");
            return "signature verification failed";
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                var paymentIntent = (com.stripe.model.PaymentIntent) event.getDataObjectDeserializer().getObject()
                        .orElse(null);
                if (paymentIntent != null) {
                    System.out.println("payment succeed");
                }
                break;
            case "payment_intent.payment_failed":
                var failedIntent = (com.stripe.model.PaymentIntent) event.getDataObjectDeserializer().getObject()
                        .orElse(null);
                if (failedIntent != null) {
                    System.out.println("payment failed");
                }
            default:
                System.out.println("unhandled type");
                break;
        }
        return "OK";
    }
}
