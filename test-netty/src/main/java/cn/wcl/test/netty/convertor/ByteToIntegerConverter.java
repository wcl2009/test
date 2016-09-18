package cn.wcl.test.netty.convertor;

public class ByteToIntegerConverter implements ByteArrayConverter<Integer> {


	public Integer convert(byte[] msg) throws Exception {
		int targets = (msg[0] & 0xff) | ((msg[1] << 8) & 0xff00) // | 表示安位或
				| ((msg[2] << 24) >>> 8) | (msg[3] << 24);
		return targets;
	}

}
