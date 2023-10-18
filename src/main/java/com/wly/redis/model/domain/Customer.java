package com.wly.redis.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("Customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String cname;

    private Integer age;

    private String phone;

    private Byte sex;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", cname='" + cname + '\'' +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", sex=" + sex +
                ", birth=" + birth +
                '}';
    }

    private Date birth;
}
