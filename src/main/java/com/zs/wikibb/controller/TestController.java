package com.zs.wikibb.controller;

import com.zs.wikibb.domain.Test;
import com.zs.wikibb.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
/*
* Restful风格：GET（查找） ,POST(新增),PUT（修改）,DELETE（删除）
* 正常情况：user？id=1
* Restful：/user/1
* */
    @Value("${test.hello:TEST}")
    //优先输出配置项中的值也就是hello4
    private String testHello;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private TestService testService;

    @GetMapping("/hello")
    public String hello(){
        return "Hello World"+testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello World!Post,"+name;
    }

    @GetMapping("/test/list")
    public List<Test> list(){
        return testService.list();
    }

    //通过以下两个方法来判断token是否真的注入进去
    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }
}
