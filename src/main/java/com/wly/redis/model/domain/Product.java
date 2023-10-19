package com.wly.redis.model.domain;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("Product")
@ApiModel(value = "聚划算活动product信息")
public class Product {

    private Long id;

    private String name;

    private Integer price;
    // 商品详情
    private String detail;
}
