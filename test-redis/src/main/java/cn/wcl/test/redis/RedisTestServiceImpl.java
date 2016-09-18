package cn.wcl.test.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTestServiceImpl implements RedisTestService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Override
	public void set(String key, String val) {
		redisTemplate.opsForValue().set(key, val);
	}

	@Override
	public String get(String key) {
		return redisTemplate.opsForValue().get(key).toString();
	}

}
