package com.stanny.nearpal.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Auther: husl
 * @Date: 2019/8/11 12:06
 * @Description:
 */
@Component
public class RedisClientUtil<T> {

    @Autowired
    private JedisPool jedisPool;

    public boolean isEnable() {
        try {
            return jedisPool.getResource() != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void set(String key, String value) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    public String get(String key) throws Exception {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    public void setobj(String key, Object object) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key.getBytes(), SerializeUtil.serialize(object));
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    public Object getObject(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] bytes = jedis.get(key.getBytes());
            return SerializeUtil.unserialize(bytes);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    public void expire(String key, int value) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.expire(key, value);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }


}