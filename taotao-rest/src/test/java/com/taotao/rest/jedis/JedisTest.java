package com.taotao.rest.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class JedisTest {
	/*
	 * jedis单机版测试
	 */
	@Test
	public void testJedisSingle(){
		//创建jedis对象
		Jedis jedis = new Jedis("192.168.27.130", 6379); 
		//jedis命令
		jedis.set("key1", "jedisTest");
		System.out.println(jedis.get("key1"));
		//关闭jedis
		jedis.close();
	}
	
	@Test
	public void testJedisCluster(){
		HashSet<HostAndPort> nodes = new HashSet<HostAndPort>();
		nodes.add(new HostAndPort("192.168.27.130", 7001));
		nodes.add(new HostAndPort("192.168.27.130", 7002));
		nodes.add(new HostAndPort("192.168.27.130", 7003));
		nodes.add(new HostAndPort("192.168.27.130", 7004));
		nodes.add(new HostAndPort("192.168.27.130", 7005));
		nodes.add(new HostAndPort("192.168.27.130", 7006));
		JedisCluster cluster = new JedisCluster(nodes);
		cluster.set("key1", "cluster");
		System.out.println(cluster.get("key1"));
		cluster.close();
	}
	@Test
	public void testSpringJedisCluster() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		JedisCluster jedisCluster =  (JedisCluster) applicationContext.getBean("redisClient");
		String string = jedisCluster.get("key1");
		System.out.println(string);
		jedisCluster.close();
	}

}
