package com.kinny.content;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author qgy
 * @create 2019/6/4 - 9:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext*.xml")
public class RedisTest {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testPing() {
        this.redisTemplate.boundValueOps("test").set("value");
    }



}
