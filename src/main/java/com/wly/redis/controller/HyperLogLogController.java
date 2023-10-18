package com.wly.redis.controller;


import com.wly.redis.service.HyperLogLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = "淘宝亿级UV的Redis统计方案")
@RequestMapping("/yperLogLog")
public class HyperLogLogController {

    @Autowired
    private HyperLogLogService hyperLogLogService;

    @ApiOperation("获得ip去重后的UV统计访问量")
    @GetMapping("/uv")
    public long uv() {
        return hyperLogLogService.uv();
    }
}
