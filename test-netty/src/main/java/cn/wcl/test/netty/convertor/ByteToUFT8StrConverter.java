package cn.wcl.test.netty.convertor;

public class ByteToUFT8StrConverter implements ByteArrayConverter<String> {

	public String convert(byte[] msg) throws Exception {
		return new String(msg, "utf-8");
	}

}
