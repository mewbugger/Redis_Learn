package com.wly.redis.controller;

import com.wly.redis.service.GeoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.geo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/geo")
@Api(tags = "美团地图位置附件的酒店推送GEO")
public class GeoController {
    @Autowired
    private GeoService geoService;

    @ApiOperation("添加坐标geoAdd")
    @GetMapping("/geoAdd")
    public String geoAdd() {
        return geoService.geoAdd();
    }

    @ApiOperation("获取经纬度坐标geoPos")
    @GetMapping("/geoPos")
    public Point geoPos(String member) {
        return geoService.geoPos(member);
    }

    @ApiOperation("获取经纬度生成的base32编码值geohash")
    @GetMapping("/geoHash")
    public String geoHash(String member) {
        return geoService.geoHash(member);
    }

    @ApiOperation("获取两个给定位置之间的距离")
    @GetMapping("/geoDist")
    public Distance geoDist(String member1, String member2) {
        return geoService.geoDist(member1, member2);
    }

    @ApiOperation("通过经度维度查找上海外滩附件的")
    @GetMapping("/geoRadius")
    public GeoResults radiusByXY() {
        return geoService.radiusByXY();
    }

    @ApiOperation("通过地方查找附件，本例写死上海中心作为地址")
    @GetMapping("/geoRadiusByMember")
    public GeoResults radiusByMember() {
        return geoService.radiusByMember();
    }
}
