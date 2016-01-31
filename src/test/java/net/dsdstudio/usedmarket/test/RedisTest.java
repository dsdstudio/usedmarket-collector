package net.dsdstudio.usedmarket.test;

import net.dsdstudio.usedmarket.config.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhkim on 2016. 1. 14..
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AppConfig.class)
public class RedisTest {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void test() {
        Map<String,String> m = new HashMap<>();
        m.put("title", "a");

        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps("board.data");
        ops.putAll(m);
    }
}
