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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            boolean isEnable = jedis != null;
            if (isEnable) jedis.close();
            return isEnable;
        } catch (Exception e) {
            return false;
        } finally {
            jedis.close();
        }
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
            if (jedis.exists(key)) {
                return jedis.get(key);
            }
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            //返还到连接池
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        } catch (Exception e) {
            return null;
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