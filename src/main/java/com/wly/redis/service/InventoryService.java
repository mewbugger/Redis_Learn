package com.wly.redis.service;


import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${server.port}")
    private String port;


/*
    //问题：没有考虑可重入性，即同一个线程在已经持有锁的情况下，再去执行申请锁的操作，此时应该直接放行
    public String sale() {
        String retMessage = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();
        // 不用递归了，高并发下容易出错，我们用自旋替代递归方法重试调用，也不用if了，用while来替代
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue,30L, TimeUnit.SECONDS)) {
            //暂停20毫秒，自旋重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            // 1.查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            // 2.判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            // 3.扣减库存，每次减少一个
            if (inventoryNumber > 0) {
                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品，库存剩余：" + inventoryNumber;
                System.out.println(retMessage + "\t" + "服务端口号" + port);
            } else {
                retMessage = "商品卖完了";
            }
        } finally {
            // 只能删除自己的key，不能删除其他线程的
            // 使用lua脚本，保证原子性
            String luaScript =
                    "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                            "return redis.call('del',KEYS[1] else return 0 end";

            stringRedisTemplate.execute(new DefaultRedisScript<>(luaScript, Boolean.class), Arrays.asList(key), uuidValue);
        }
        return retMessage + "服务端口号" + port;
    }*/

/*
    //finally中的语句仍然不是原子性，仍然有可能在删除之前宕机，改进方法：使用lua脚本
    public String sale() {
        String retMessage = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();
        // 不用递归了，高并发下容易出错，我们用自旋替代递归方法重试调用，也不用if了，用while来替代
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue,30L, TimeUnit.SECONDS)) {
            //暂停20毫秒，自旋重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            // 1.查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            // 2.判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            // 3.扣减库存，每次减少一个
            if (inventoryNumber > 0) {
                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品，库存剩余：" + inventoryNumber;
                System.out.println(retMessage + "\t" + "服务端口号" + port);
            } else {
                retMessage = "商品卖完了";
            }
        } finally {
            // 只能删除自己的key，不能删除其他线程的
            if (stringRedisTemplate.opsForValue().get(key).equalsIgnoreCase(uuidValue)) {
                stringRedisTemplate.delete(key);
            }
        }
        return retMessage + "服务端口号" + port;
    }*/

/*
    //实际业务时间超过了设置的锁过期时间，则会删除别的线程的锁，应该只能自己删除自己的锁
    public String sale() {
        String retMessage = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();
        // 不用递归了，高并发下容易出错，我们用自旋替代递归方法重试调用，也不用if了，用while来替代
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue,30L, TimeUnit.SECONDS)) {
            //暂停20毫秒，自旋重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            // 1.查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            // 2.判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            // 3.扣减库存，每次减少一个
            if (inventoryNumber > 0) {
                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品，库存剩余：" + inventoryNumber;
                System.out.println(retMessage + "\t" + "服务端口号" + port);
            } else {
                retMessage = "商品卖完了";
            }
        } finally {
            stringRedisTemplate.delete(key);
        }
        return retMessage + "服务端口号" + port;
    }*/

/*
    //v3.2 存在的问题
    //部署了微服务的Java程序机器宕机了，代码层面根本没有走到finally，没办法保证解锁，所以该key一直存在，需要加上锁过期时间
    public String sale() {
        String retMessage = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();
        // 不用递归了，高并发下容易出错，我们用自旋替代递归方法重试调用，也不用if了，用while来替代
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue)) {
            //暂停20毫秒，自旋重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            // 1.查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            // 2.判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            // 3.扣减库存，每次减少一个
            if (inventoryNumber > 0) {
                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品，库存剩余：" + inventoryNumber;
                System.out.println(retMessage + "\t" + "服务端口号" + port);
            } else {
                retMessage = "商品卖完了";
            }
        } finally {
            stringRedisTemplate.delete(key);
        }
        return retMessage + "服务端口号" + port;
    }*/


/*
    v3.1 递归重试，容易导致栈溢出，所以不太推荐，另外，高并发唤醒后推荐使用while判断而不是if
    public String sale() {
        String retMessage = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+ ":" + Thread.currentThread().getId();

        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue);
        //flag=false，抢不到的线程要继续重试...
        if (!flag) {
            //暂停20毫秒，进行递归重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sale();
        } else {
            //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
            try {
                // 1.查询库存信息
                String result = stringRedisTemplate.opsForValue().get("inventory001");
                // 2.判断库存是否足够
                Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
                // 3.扣减库存，每次减少一个
                if (inventoryNumber > 0) {
                    stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
                    retMessage = "成功卖出一个商品，库存剩余：" + inventoryNumber;
                    System.out.println(retMessage + "\t" + "服务端口号" + port);
                } else {
                    retMessage = "商品卖完了";
                }
            } finally {
                stringRedisTemplate.delete(key);
            }
        }
        return retMessage + "服务端口号" + port;
    }
*/

/*
    单机版加锁配合Nginx和Jmeter压测后，不满足高并发分布式锁的性能要求，出现超卖
    private Lock lock = new ReentrantLock();
    public String sale() {

        String retMessage = "";

        lock.lock();
        try {
            // 1.查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            // 2.判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            // 3.扣减库存，每次减少一个
            if (inventoryNumber > 0) {
                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品，库存剩余：" + inventoryNumber;
                System.out.println(retMessage + "\t" + "服务端口号" + port);
            } else {
                retMessage = "商品卖完了";
            }
        } finally {
            lock.unlock();
        }

        return retMessage + "服务端口号" + port;
    }*/
}
