package com.wly.redis.service.Impl;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.wly.redis.service.GuavaBloomFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Service
@Slf4j
public class GuavaBloomFilterServiceImpl implements GuavaBloomFilterService {

    // 1.定义一个常亮
    public static final int _1W = 10000;
    // 2.定义我们guava布隆过滤器的初始容量
    public static final int SIZE = 100 * _1W;
    // 3.误判率，它越小误判的个数也就越少
    public static double fpp = 0.03;
    // 4.创建guava布隆过滤器
    public static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), SIZE, fpp);
    @Override
    public void guavaBloomFilter() {
        // 1.先让bloomFilter加入100W白名单数据
        for (int i = 1; i < SIZE; i++) {
            bloomFilter.put(i);
        }
        // 2.故意取10W个不在合法范围内的数据，来进行误判率的演示
        ArrayList<Integer> list = new ArrayList<>(10 * _1W);

        // 3.验证
        for (int i = SIZE + 1; i < SIZE + (10 * _1W); i++) {
            if (bloomFilter.mightContain(i)) {
                log.info("被误判了：{}", i);
                list.add(i);
            }
        }
        log.info("误判总数量：{}", list.size());
    }
}
