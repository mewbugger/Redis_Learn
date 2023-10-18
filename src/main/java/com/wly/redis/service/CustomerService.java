package com.wly.redis.service;

import com.wly.redis.model.domain.Customer;

public interface CustomerService {

    void addCustomer(Customer customer);

    Customer queryCustomerById(Integer customerId);

    Customer queryCustomerByIdWithBloomFilter(Integer customerId);

}
