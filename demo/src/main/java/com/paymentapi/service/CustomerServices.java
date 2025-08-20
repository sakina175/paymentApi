package com.paymentapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.paymentapi.dto.CustomerResponse;
import com.paymentapi.model.Customers;
import com.paymentapi.repositry.CustomerRepositry;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServices {
    private final CustomerRepositry customerRepositry;
    
    public List<Customers> getAll(){
        List<Customers> customers=customerRepositry.findAll();
        return customers;
    } 
    public ResponseEntity<CustomerResponse> getCustByEmail(String custEmail){
        Customers localCustomer = customerRepositry.findByEmail(custEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found in local DB"));
        
        CustomerResponse customerResponse=new CustomerResponse();
        customerResponse.setEmail(localCustomer.getEmail());
        customerResponse.setId(localCustomer.getId());
        customerResponse.setStripeCustomerId(localCustomer.getStripeCustomerId());
        
        return ResponseEntity.ok(customerResponse);
    }

    public Map<String,Object> createCustomer(Map<String,Object> requestData) throws StripeException{
        Map<String,Object> params=new HashMap<>();
        params.put("name",requestData.get("name").toString());
        params.put("email",requestData.get("email").toString());

        Customer stripeCustomer=Customer.create(params); 

        Customers localCustomers=new Customers();
        localCustomers.setName(requestData.get("name").toString());
        localCustomers.setEmail(requestData.get("email").toString());
        localCustomers.setStripeCustomerId(stripeCustomer.getId());
        Customers newCustomer=customerRepositry.save(localCustomers);

        Map<String,Object> response=new HashMap<>();
        response.put("stripeCustomerId", stripeCustomer.getId());
        response.put("customerId", newCustomer.getId());
        
        return response;
    }
}