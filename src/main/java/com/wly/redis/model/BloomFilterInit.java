package com.wly.redis.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Slf4j
public class BloomFilterInit {

    @Resource
    private RedisTemplate redisTemplate;

    @PostConstruct//初始化布隆过滤器白名单
    public void init() {
        // 1.白名单客户加载到布隆过滤器
        String key = "customer:0";
        // 2.计算hashValue，由于存在计算出来负数的可能，我们取绝对值
        int hashValue = Math.abs(key.hashCode());
        // 3.通过hashValue和2的32次方取余，获得对应的下标坑位
        long index = (long) (hashValue % Math.pow(2, 32));
        // 4.设置redis里面的bitmap对应类型白名单：whitelistCustomer的坑位，将值设置为1
        redisTemplate.opsForValue().setBit("whitelistCustomer", index, true);
        log.info("customer:0，在whitelistCustomer的坑位是：" + index);
    }
}
