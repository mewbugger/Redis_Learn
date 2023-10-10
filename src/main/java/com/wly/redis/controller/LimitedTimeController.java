package com.wly.redis.controller;


import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀抢购场景
 */
@RestController
@RequestMapping("/limitedTimeOffers")
public class LimitedTimeController {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 可能会出现超卖情况，假设三个线程都进入了if (stock > 0) ，实际上卖掉了三个，但是各自这里都只显示仅仅卖掉了自己的
     *
     * @return
     */
    @GetMapping("/deduct_stock1")
    public String deductStack1() {
        //此处是模拟，实际情况中商品id应该是通过方法中的参数传递过来
        String lockKey = "productId";
        /*
        *   注意：
        *       不能先创建锁，再设置超时时间，一定要创建锁的同时，就设置超时时间
        *       因为，如果分开来设置，在创建锁和设置超时时间中间出现了宕机或异常，仍然无法释放锁
        */
        String clientId = UUID.randomUUID().toString();
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, clientId, 10, TimeUnit.SECONDS);
        RLock redissonLock = redisson.getLock(lockKey);
        redissonLock.lock();
        if (!result) {
            return "error";
        }
        try {
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + "");
                System.out.println("扣减成功，剩余库存：" + realStock);
            } else {
                System.out.println("扣减失败，库存不足");
            }
        } finally {
            redissonLock.unlock();
        }
        return "end";
    }

    @GetMapping("/deduct_stock")
    public String deductStack() {

        //分布式锁的key
        String lockKey = "product_id";

        //分布式锁
        //jedis.setnx(k, v); 当k不存在的时候，执行set操作
        //Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(localKey, "wly");
        /**
         *      1.stringRedisTemplate.opsForValue().setIfAbsent(localKey, "wly")
         *      2.stringRedisTemplate.expire(localKey, 10, TimeUnit.SECONDS);
         *      这种方法仍然有问题，例如创建完分布式锁后，电脑宕机了，就没有设置超时时间
         */
        //创建分布式锁的同时指定超时时间。
        //把分布式锁的value指定为clientId,即每个用户创建的分布式锁的key相同，value不同
        /**
         *  该操作解决的问题是：
         *      用户1创建锁后，超过了锁的超时时间，但是业务没有执行完，不会执行删除锁的操作，但是锁已经过期了
         *      此时用户2可以创建锁，在锁没有过期的情况下，用户1的执行了删除锁的逻辑，即把用户2的锁删除了
         *      以此类推，当并发量很大的时候，用户会删除别的用户创建的分布式锁
         */
        String clientId = UUID.randomUUID().toString();
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, clientId, 10, TimeUnit.SECONDS);
        if (!result) {
            return "plz wait";
        }

        RLock redissonLock = redisson.getLock(lockKey);
        // redissonLock.lock() 封装了setIfAbsent(localKey, clientId, 10, TimeUnit.SECONDS);
        redissonLock.lock();

        //串行执行，仅仅适用于单机情况，局限于单体web服务内部，而redis可能是集群的
        //jedis.get("stock")
        try {
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                int realStack = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStack + "");
                System.out.println("扣减成功，剩余库存：" + realStack);
            } else {
                System.out.println("扣减失败，库存不足");
            }
        } finally {
            //判断要执行删除锁的用户所要删除的锁是不是自己创建的
            /**
             *      问题：执行完下面这句判断语句后，锁超时，用户2创建了锁，接下来的删除操作，删除的是用户2创建的锁
             *      解决方法：使用redisson分布式锁,自动实现锁续期（即，锁要超时了，但是业务还是执行，为锁延长超时时间）
             *
             */
            redissonLock.unlock();
//            if (clientId.equals(stringRedisTemplate.opsForValue().get(localKey))) {
//                stringRedisTemplate.delete(localKey);
//            }

        }
        return "end";
    }
}

