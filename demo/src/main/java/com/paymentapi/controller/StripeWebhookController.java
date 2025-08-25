package com.paymentapi.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymentapi.service.StripeWebhookServices;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/webhook")
public class StripeWebhookController {
    StripeWebhookServices stripeWebhookServices;

    @PostMapping("/stripe")
    public String handleStripeEvent(HttpServletRequest request, @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws IOException {
        String response = stripeWebhookServices.handleStripeEvent(request, payload, sigHeader);
        return response;
    }

}
