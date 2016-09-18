package cn.wcl.test.netty.server;

import java.util.List;
import java.util.Map;

import cn.wcl.test.netty.constants.RtCode;

public interface ProtocalHandler {

	RtCode handle(String type,String packageName, Map<String, List<String>> file)
			throws Exception;

}
