package com.eventimist.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class EventimistBackendApplicationTests {


	@Autowired
	private StringRedisTemplate redisTemplate;

	@Test
	void redisConnectionTest() {

		System.out.println("TEST STARTED");

		redisTemplate.opsForValue().set("test-key", "Hello Redis");

		String value = redisTemplate.opsForValue().get("test-key");

		System.out.println("Redis value: " + value);
	}



}