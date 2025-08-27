package com.paymentapi.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentapi.model.Transactions;
import com.paymentapi.repositry.TransactionRepositry;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeWebhookServices {
    TransactionRepositry transactionRepositry;

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

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // fallback to raw JSON
            try {
                ObjectMapper mapper = new ObjectMapper();

                // parse payload into JSON
                JsonNode jsonNode = mapper.readTree(payload);

                // event.data.object holds the real object
                JsonNode dataObject = jsonNode.get("data").get("object");

                if ("payment_intent.succeeded".equals(event.getType()) ||
                        "payment_intent.payment_failed".equals(event.getType()) ||
                        "payment_intent.canceled".equals(event.getType()) ||
                        "payment_intent.processing".equals(event.getType())||
                        "charge.succeeded".equals(event.getType())||
                        "charge.updated".equals(event.getType()) ){

                    // convert JSON node into PaymentIntent
                    PaymentIntent paymentIntent = mapper.treeToValue(dataObject, PaymentIntent.class);
                    stripeObject = paymentIntent;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
            case "payment_intent.payment_failed":
            case "payment_intent.canceled":
            case "payment_intent.processing":
            case "charge.succeeded":
            case "charge.updated":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                if (paymentIntent != null) {
                    Transactions localTransaction = transactionRepositry
                            .findByStripePaymentIntentId(paymentIntent.getId());
                    if (localTransaction != null) {
                        localTransaction.setStatus(event.getType());

                        if ("payment_intent.payment_failed".equals(event.getType())) {
                            localTransaction.setFailureReason(paymentIntent.getLastPaymentError() != null
                                    ? paymentIntent.getLastPaymentError().getCode()
                                    : "UNKNOWN");
                        }
                        transactionRepositry.save(localTransaction);
                    }
                }
                break;

            case "checkout.session.completed":
                System.out.println("Checkout session completed → retrieve PaymentIntent from session");
                break;

            default:
                System.out.println("⚠️ Unhandled event type: " + event.getType());
                break;
        }

        return "OK";
    }
}
