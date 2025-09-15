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
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeWebhookServices {
    private final TransactionRepositry transactionRepositry;

    public StripeWebhookServices(TransactionRepositry transactionRepositry){
        this.transactionRepositry=transactionRepositry;
    }

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    public String handleStripeEvent(HttpServletRequest request, @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws IOException {
        Event event;
        try {
            //verify sign
            event=Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            System.out.println("signature error:"+e);
            return "Invalid Signature";
        }

        try {
            ObjectMapper mapper=new ObjectMapper();
            JsonNode root=mapper.readTree(payload);
            JsonNode dataObject=root.path("data").path("object");

            String paymentIntentId=dataObject.has("id")? dataObject.get("id").asText():null;
            if(paymentIntentId==null){
                System.out.println("Payment intent id missing in payload");
                return "Missing Payment Intent Id";
            }

            Transactions localTransactions=transactionRepositry.findByStripePaymentIntentId(paymentIntentId);
            if(localTransactions==null){
                System.out.println("No matching local transaction for PaymentIntent ID: " + paymentIntentId);
                return "Transaction not found";
            }

            String eventType=event.getType();
            localTransactions.setStatus(eventType);

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
