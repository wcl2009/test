package cn.wcl.test.rebbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

//QueueListenter.java
@Component("queueListenter")
public class QueueListenter implements MessageListener {
	public QueueListenter() {
		System.out.println("[****************] MessageQueue waiting for messages...");
	}

	@Override
	public void onMessage(Message msg) {
		try {
			System.out.println("#############received");
			System.out.print(msg.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}