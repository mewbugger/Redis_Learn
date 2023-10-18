package com.wly.redis.controller;


import com.wly.redis.model.domain.Customer;
import com.wly.redis.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/customer")
@Api(tags = "客户Customer接口+布隆过滤器讲解")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @ApiOperation("数据库初始化2条Customer记录插入")
    @PostMapping("/add")
    public void addCustomer() {
        for (int i = 0; i < 2; i++) {
            Customer customer = new Customer();

            customer.setId(i);
            customer.setCname("customer" + i);
            customer.setAge(new Random().nextInt(30) + 1);
            customer.setPhone("1371111XXXX");
            customer.setSex((byte) new Random().nextInt(2));
            customer.setBirth(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

            customerService.addCustomer(customer);
        }
    }

    @ApiOperation("单个用户查询，按照id查询")
    @GetMapping("/queryOne/{customerId}")
    public Customer queryOneCustomerById(@PathVariable int customerId) {
        return customerService.queryCustomerById(customerId);
    }

    @ApiOperation("布隆过滤器，按照id查询")
    @GetMapping("/customerBloomFilter/{customerId}")
    public Customer queryOneCustomerByIdWithBloomFilter(@PathVariable int customerId) {
        Customer customer = customerService.queryCustomerByIdWithBloomFilter(customerId);
        return customer;
    }
}
