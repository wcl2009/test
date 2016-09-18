package cn.wcl.test.rebbitmq;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//TestQueue.java
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context.xml" })
public class TestQueue {
	@Autowired
	MQProducer mqProducer;

	final String query_key = "123.213.1";

	@Test
	public void send() {
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("data", "hello,rabbmitmq!");
		mqProducer.sendDataToQueue(query_key, msg);
	}

}