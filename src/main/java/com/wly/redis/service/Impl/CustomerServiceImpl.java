package com.wly.redis.service.Impl;

import com.wly.redis.mapper.CustomerMapper;
import com.wly.redis.model.domain.Customer;
import com.wly.redis.service.CustomerService;
import com.wly.redis.utils.CheckUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    public static final String CACHE_KEY_CUSTOMER = "customer:";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CheckUtils checkUtils;


    @Override
    public void addCustomer(Customer customer) {
        int result = customerMapper.insertOneCustomer(customer);
        if (result > 0) {
            //mysql插入成功,需要重新查询一次，将数据捞出来，写进redis
            Customer customerResult = customerMapper.queryOneCustomerById(customer.getId());
            //redis缓存key
            String key = CACHE_KEY_CUSTOMER + customer.getId();
            //捞出来的数据写进redis
            redisTemplate.opsForValue().set(key, customerResult);
        }
    }

    @Override
    public Customer queryCustomerById(Integer customerId) {
        Customer customer = null;
        //缓存redis的key名称
        String key = CACHE_KEY_CUSTOMER + customerId;

        // 1.先去redis查询
        customer = (Customer) redisTemplate.opsForValue().get(key);
        // 2.redis有直接返回，没有再进去查询mysql
        if (customer == null) {
            // 3.查询mysql
            customer = customerMapper.queryOneCustomerById(customerId);
            // 3.1mysql有，redis无
            if (customer != null) {
                // 3.2把mysql查询出来的数据放进 redis
                redisTemplate.opsForValue().set(key, customer);
            }
        }
        return customer;
    }

    @Override
    public Customer queryCustomerByIdWithBloomFilter(Integer customerId) {
        Customer customer = null;
        //缓存redis的key名称
        String key = CACHE_KEY_CUSTOMER + customerId;

        // 布隆过滤器check，无是绝对无，有是可能有
        //=================================
        if (!checkUtils.checkWithBloomFilter("whitelistCustomer", key)) {
            log.info("白名单无此顾客，不可访问：" + key);
            //执行停止操作
        }


        // 1.先去redis查询
        customer = (Customer) redisTemplate.opsForValue().get(key);
        // 2.redis有直接返回，没有再进去查询mysql
        if (customer == null) {
            // 3.查询mysql
            customer = customerMapper.queryOneCustomerById(customerId);
            // 3.1mysql有，redis无
            if (customer != null) {
                // 3.2把mysql查询出来的数据放进 redis
                redisTemplate.opsForValue().set(key, customer);
            }
        }
        return customer;
    }
}
