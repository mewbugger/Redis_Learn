package com.wly.redis;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import io.swagger.models.auth.In;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testGuavaWithBloomFilter() {
        // 1.创建guava版布隆过滤器
        // 参数1:指定过滤的数据类型    参数2:存储的数量
        BloomFilter<Integer> integerBloomFilter = BloomFilter.create(Funnels.integerFunnel(), 100);
        // 2.判断指定的元素是否存在
        System.out.println(integerBloomFilter.mightContain(1));
        System.out.println(integerBloomFilter.mightContain(2));

        System.out.println("==================================================");
        // 3.将元素新增进入布隆过滤器
        integerBloomFilter.put(1);
        integerBloomFilter.put(2);
        System.out.println(integerBloomFilter.mightContain(1));
        System.out.println(integerBloomFilter.mightContain(2));
    }

}
