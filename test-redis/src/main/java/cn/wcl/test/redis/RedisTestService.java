package cn.wcl.test.redis;

public interface RedisTestService {

	public void set(String key, String val);

	public String get(String key);
}
