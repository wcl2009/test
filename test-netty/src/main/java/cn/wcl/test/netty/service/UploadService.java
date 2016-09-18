package cn.wcl.test.netty.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cn.wcl.test.netty.constants.RtCode;
import cn.wcl.test.netty.server.ProtocalHandler;
@Service("protocalHandler001")
public class UploadService implements ProtocalHandler {

	@Override
	public RtCode handle(String type,String packageName, Map<String, List<String>> file)
			throws Exception {
//		System.out.println("001");
		return RtCode.SUCCESS;
	}

}
