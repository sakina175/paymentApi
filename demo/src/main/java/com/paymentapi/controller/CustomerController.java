package com.paymentapi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymentapi.dto.CustomerResponse;
import com.paymentapi.model.Customers;
import com.paymentapi.service.CustomerServices;
import com.stripe.exception.StripeException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerServices customerServices;

    @GetMapping("/")
    public List<Customers> getAll(){
        List<Customers> customers=customerServices.getAll();
        return customers;
    }

    @GetMapping("/{customerEmail}")
    public ResponseEntity<CustomerResponse> getCustById(@PathVariable String customerEmail){
        ResponseEntity<CustomerResponse> customers=customerServices.getCustByEmail(customerEmail);
        return customers;
    }

    @PostMapping
    @RequestMapping("/create")
    public Map<String,Object> createCustomer(
        @RequestBody Map<String,Object> requestData
    ) throws StripeException{
        Map<String,Object> response= customerServices.createCustomer(requestData);
        return response;
    }

}