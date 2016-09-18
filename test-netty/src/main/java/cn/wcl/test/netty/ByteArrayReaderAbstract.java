package cn.wcl.test.netty;

import cn.wcl.test.netty.convertor.ByteArrayConverter;

public class ByteArrayReaderAbstract implements IByteArrayReader {

	protected byte[] msg;

	protected int read_length = 0;

	public ByteArrayReaderAbstract(byte[] msg) {
		this.msg = msg;
	}

	ByteArrayReaderAbstract() {
	}

	public byte[] read() {
		return msg;
	}

	public <T> T read(ByteArrayConverter<T> converter) throws Exception {
		return converter.convert(msg);
	}

	public byte[] read(int start, int end) {
		if (msg == null || end - start < 0)
			return null;
		byte[] res = new byte[end - start];
		System.arraycopy(msg, start, res, 0, end - start);
		read_length += (end - start);
		return res;
	}

	public <T> T read(int start, int end, ByteArrayConverter<T> converter)
			throws Exception {
		if (msg == null || end - start < 0)
			return null;
		byte[] res = new byte[end - start];
		System.arraycopy(msg, start, res, 0, end - start);
		read_length += (end - start);
		return converter.convert(res);
	}

	public void release() {
		msg = null;
	}

	public int readLength() {
		return read_length;
	}
}
