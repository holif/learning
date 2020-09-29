package com.len.boot.redis.controller;

import com.len.boot.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: jone
 * @date: Created in 2020/9/25 10:44 上午
 * @description:
 */
@RestController
public class ChaceConntroller {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/add")
    public Object add(@RequestParam String key,@RequestParam String value){
        return redisUtil.add(key,value);
    }

    @GetMapping("/get")
    public Object get(@RequestParam String key){
        return redisUtil.get(key);
    }

}
