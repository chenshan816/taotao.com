package com.taotao.order.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.taotao.order.dao.JedisClient;


public class JedisClientSingleImpl implements JedisClient{
	
	@Autowired
	private JedisPool jedisPool;
	
	@Override
	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.get(key);
		jedis.close();
		return  result;
	}

	@Override
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.set(key, value);
		jedis.close();
		return  result;
	}

	@Override
	public String hget(String hkey, String key) {
		Jedis jedis = jedisPool.getResource();
		String result = jedis.hget(hkey, key);
		jedis.close();
		return  result;
	}

	@Override
	public long hset(String hkey, String key, String value) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.hset(hkey, key, value);
		jedis.close();
		return  result;
	}

	@Override
	public long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.incr(key);
		jedis.close();
		return  result;
	}

	@Override
	public long decr(String key) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.decr(key);
		jedis.close();
		return  result;
	}

	@Override
	public long expire(String key, int seconds) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.expire(key, seconds);
		jedis.close();
		return  result;
	}

	@Override
	public long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.ttl(key);
		jedis.close();
		return  result;
	}

	@Override
	public long del(String key) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.del(key);
		jedis.close();
		return  result;
	}

	@Override
	public long hdel(String hkey, String key) {
		Jedis jedis = jedisPool.getResource();
		long result = jedis.hdel(hkey,key);
		jedis.close();
		return  result;
	}

}
