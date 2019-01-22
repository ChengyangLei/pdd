package com.spider.util;
import com.alibaba.fastjson.JSONObject;
import com.spider.model.SpiderInfo;
import com.tomcong.config.Configuration;
import org.apache.commons.codec.binary.Base64;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.io.UnsupportedEncodingException;
public class RedisUtil {
	private final static int defaultPort = 10000;
	private final static int timeout = 6000;
	private final static String password = "Summer001";
	private final static int defaultDbIndex = 1;
	private final static int maxActive = 10;
	private final static int maxIdle = 50;
	private final static int minIdle =1;
	private final static int maxWait = 30;
	private final static String host = "114.67.85.93" ;
	private static JedisPoolConfig config = new JedisPoolConfig();
	private static JedisPool jedisPool;
	static{
		config.setMaxIdle(maxIdle);
		config.setMaxTotal(maxActive);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWait*1000);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		config.setTimeBetweenEvictionRunsMillis(30000);
		config.setNumTestsPerEvictionRun(10);
		config.setMinEvictableIdleTimeMillis(60000);
		jedisPool = new JedisPool(config, host, defaultPort, timeout, password, defaultDbIndex);
	}
	public static Jedis getJedis(int index) {
		JedisPool pool = jedisPool;
		int errorCount =0;
		Jedis jedis = pool.getResource();
		while(jedis==null&&errorCount<5){
			jedis = pool.getResource();
			errorCount++;
		}
		return jedis;
	}

	public static boolean setObject(long buyUrlId, Object obj) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			if (jedis == null) return false;
			String key = String.format("pdd-%d-html", buyUrlId);
			String value = JSONObject.toJSONString(obj);
			String result = jedis.set(key, value, "NX", "EX", 24 * 60 * 60);
			return "OK".equals(result);
		}catch (RuntimeException e){
			e.fillInStackTrace();
		}finally {
			if(jedis!=null)jedis.close();
		}
		return false;
	}
	public static String get(long buyUrlId){
		Jedis jedis = null;
		try {
			jedis = getJedis();
			if (jedis == null)return null;
			String key = String.format("pdd-%d-html",buyUrlId);
			return jedis.get(key);
		}catch (RuntimeException e){
			e.fillInStackTrace();
		}finally {
			if(jedis!=null)jedis.close();
		}
		return null;

	}
	public static String getJson(long buyUrlId){
		Jedis jedis = null;
		try {
			jedis = getJedis();
			if (jedis == null)return null;
			String key = String.format("pdd-%d-json",buyUrlId);
			return jedis.get(key);
		}catch (RuntimeException e){
			e.fillInStackTrace();
		}finally {
			if(jedis!=null)jedis.close();
		}
		return null;

	}
	public static void del(long buyUrlId){
		int index = (int) (buyUrlId % 100);
		Jedis jedis = null;
		try {
			jedis = getJedis(index);
			if (jedis == null) return;
			String key = String.format("pdd-%d-html",buyUrlId);
			jedis.del(key);
		}catch (RuntimeException e) {
			e.fillInStackTrace();
		}finally {
			if(jedis!=null)jedis.close();
		}

	}

	public static Jedis getJedis() {
		return getJedis(defaultDbIndex);
	}

	public static void main(String[] args) {
		Jedis redis = RedisUtil.getJedis();
		redis.set("a","cc", "NX", "EX", 24 * 60 * 60);
		System.out.println(redis.get("a"));


	}
}
