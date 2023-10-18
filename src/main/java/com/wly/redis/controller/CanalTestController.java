package com.wly.redis.controller;


import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.wly.redis.domain.RedisCanalClientExample;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/canal")
public class CanalTestController {

    @GetMapping("/test")
    public void testCanal() {
        System.out.println("--------------init()--------------");

        // 创建链接canal服务器
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(RedisCanalClientExample.REDIS_IP_ADDR, 11111),
                "example",
                "",
                "");
        int batchSize = 1000;
        //空闲空转计数器
        int emptyCount = 0;
        System.out.println("--------------canal init ok, 开始监听mysql变化--------------");
        try {
            connector.connect();
            connector.subscribe("redis_learn.t_user");
            connector.rollback();
            int totalEmptyCount = 10 * RedisCanalClientExample._60SECONDS;
            while (emptyCount < totalEmptyCount) {
                System.out.println("我是canal，每秒一次正在监听：" + UUID.randomUUID().toString());
                //获取指定数量的数据
                Message mes = connector.getWithoutAck(batchSize);
                long batchId = mes.getId();
                int size = mes.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //计数器重新置零
                    emptyCount = 0;
                    RedisCanalClientExample.printEntry(mes.getEntries());
                }
                //提交确认
                connector.ack(batchId);
            }
            System.out.println("已经监听了" + totalEmptyCount + "秒，无任何消息，请重启重试......");
        } finally {
            connector.disconnect();
        }
    }
}
