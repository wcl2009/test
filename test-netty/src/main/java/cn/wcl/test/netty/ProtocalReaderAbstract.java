package cn.wcl.test.netty;

import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import cn.wcl.test.netty.convertor.ByteArrayConverter;
import cn.wcl.test.netty.convertor.ByteToIntegerConverter;
import cn.wcl.test.netty.convertor.ByteToUFT8StrConverter;

public abstract class ProtocalReaderAbstract extends ByteArrayReader {

	public ProtocalReaderAbstract(byte[] msg) {
		super(msg);
	}

	private ByteArrayConverter<?>[] converters;

	private int pos = 0;

	public Map<?, ?> readMap(LinkedMap protocalRule) throws Exception {

		if (protocalRule == null || protocalRule.size() == 0) {
			return new LinkedMap();
		}

		converters = new ByteArrayConverter[protocalRule.size()];
		bind();
		ByteArrayConverter<?> converter = null;

		// 声明结果集
		LinkedMap val = new LinkedMap();
		byte[] temp = null;

		for (int i = 0; i < protocalRule.size(); i++) {

			Object key = protocalRule.get(i);
			try {
				temp = seqRead(new Integer(protocalRule.get(key).toString()));

			} catch (Exception e) {
				System.out.println(protocalRule.get(key));
			}
			if (converters[i] == null) {
				converter = DefalutConverter.DEFAULT;
				converters[i] = converter;
			} else {
				converter = converters[i];
			}

			val.put(key, converter.convert(temp));
		}
		return val;
	}

	protected abstract void bind();

	protected ProtocalReaderAbstract bindConverter(
			ByteArrayConverter<?> converter) {
		converters[pos] = converter;
		pos++;
		return this;
	}
}

class DefalutConverter {

	public static final ByteArrayConverter<byte[]> DEFAULT = new ByteArrayConverter<byte[]>() {
		@Override
		public byte[] convert(byte[] msg) throws Exception {
			return msg;
		}
	};

	public static final ByteArrayConverter<Integer> INTEGER = new ByteToIntegerConverter();

	public static final ByteArrayConverter<String> STR_UTF8 = new ByteToUFT8StrConverter();

}
