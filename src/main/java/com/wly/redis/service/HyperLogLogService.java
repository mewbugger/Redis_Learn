package com.wly.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HyperLogLogService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @PostConstruct指示Spring容器在构造bean之后、并完成依赖注入之后，调用标记的方法进行进一步的初始化工作
     */
    //@PostConstruct
    public void initIP() {
        new Thread(() -> {
            String ip = null;
            for (int i = 0; i < 200; i++) {
                Random random = new Random();
                ip = random.nextInt(256) + "." +
                        random.nextInt(256) + "." +
                        random.nextInt(256) + "." +
                        random.nextInt(256);
                long hll = redisTemplate.opsForHyperLogLog().add("hll", ip);
                log.info("ip = {}, 该ip地址访问首页的次数 = {}", ip, hll);
                //暂停3秒
                try{
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();
    }

    public long uv() {
        //PFCOUNT
        return redisTemplate.opsForHyperLogLog().size("hll");
    }
}
