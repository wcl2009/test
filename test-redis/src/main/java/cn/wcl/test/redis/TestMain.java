package cn.wcl.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context.xml" })
public class TestMain {

	@Autowired
	RedisTestService redisTestService;

	@Test
	public void TestMethod() {
//		redisTestService.set("test1", "test123");
		System.out.println(redisTestService.get("test1"));
	}
}
