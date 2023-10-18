package com.wly.redis.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeoService {

    public static final String CITY = "city";

    @Autowired
    private RedisTemplate redisTemplate;


    public String geoAdd() {
        Map<String, Point> map = new HashMap<>();
        map.put("东方明珠", new Point(121.505267,31.244962));
        map.put("经贸大厦", new Point(121.488363,31.23839));
        map.put("上海中心", new Point(121.511937,31.239212));
        redisTemplate.opsForGeo().add(CITY, map);
        return map.toString();
    }

    public Point geoPos(String member) {
        //获取经纬度
        List<Point> position = redisTemplate.opsForGeo().position(CITY, member);
        return position.get(0);
    }

    public String geoHash(String member) {
        //geoHash算法生成的base32编码值
        List<String> hash = redisTemplate.opsForGeo().hash(CITY, member);
        return hash.get(0);
    }

    public Distance geoDist(String member1, String member2) {
        //获取两个给定位置之间的距离
        Distance distance = redisTemplate.opsForGeo().distance(CITY, member1, member2,
                RedisGeoCommands.DistanceUnit.KILOMETERS);
        return distance;
    }

    public GeoResults radiusByXY() {
        //通过经纬度查找附近的，外滩 121.497204,31.243453
        Circle circle = new Circle(121.497204, 31.243453, RedisGeoCommands.DistanceUnit.KILOMETERS.getMultiplier());
        //返回50条
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortDescending().limit(50);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(CITY, circle, args);
        return results;

    }

    public GeoResults radiusByMember() {
        //通过地方查找附近
        String member = "上海中心";
        //返回50条
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortDescending().limit(50);
        //附近10km
        Distance distance=new Distance(10, Metrics.KILOMETERS);
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults= redisTemplate.opsForGeo().radius(CITY,member, distance,args);
        return geoResults;

    }
}
