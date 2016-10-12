package cn.wcl.test.mogodb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.wcl.test.mogodb.dao.MongoTestDao;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context.xml" })
public class TestMain {

	@Autowired
	MongoTestDao mongoTestDao;

	@Test
	public void TestMethod() {
		// redisTestService.set("test1", "test123");
		mongoTestDao.addPerson(null);
	}
}
