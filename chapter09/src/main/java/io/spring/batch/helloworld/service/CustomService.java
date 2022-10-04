package io.spring.batch.helloworld.service;

import io.spring.batch.helloworld.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomService {

    public void logCustomer(Customer cust) {
        System.out.println("I just saved " + cust);
    }

    public void logCustomerAddress(String address,
                                   String city,
                                   String state,
                                   String zip) {
        System.out.println(String.format("I just saved the address:\n%s \n%s, %s \n%s", address, city, state, zip));
    }
}
