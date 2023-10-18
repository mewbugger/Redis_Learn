package com.wly.redis.mapper;

import com.wly.redis.model.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper {
    int insertOneCustomer(Customer customer);

    Customer queryOneCustomerById(Integer id);
}
