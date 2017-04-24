package com.taotao.rest.dao;

public interface JedisClient {
	
	String get(String key);
	String set(String key,String value);
	long del(String key);
	String hget(String hkey,String key);
	long hset(String hkey,String key,String value);
	long hdel(String hkey,String key);
	long incr(String key);
	long decr(String key);
	long expire(String key,int seconds);
	long ttl(String key);
}
