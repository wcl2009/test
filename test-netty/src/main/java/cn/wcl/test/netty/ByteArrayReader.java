package cn.wcl.test.netty;

import cn.wcl.test.netty.convertor.ByteArrayConverter;

public class ByteArrayReader extends ByteArrayReaderAbstract {

	private int rpos = 0;

	protected static String DEFAULT_CHARSET = "UTF-8";

	public ByteArrayReader(byte[] msg) {
		super(msg);
	}

	public String strValue() throws Exception {
		return strValue(DEFAULT_CHARSET);
	}

	public String strValue(String charset) throws Exception {
		return new String(read(), charset);
	}

	public byte[] seqRead() throws Exception {
		byte[] byte_ = read(rpos, msg.length);
		rpos = msg.length;
		return byte_;
	}

	public byte[] seqRead(int len) throws Exception {
		byte[] byte_ = read(rpos, rpos + len);
		rpos += len;
		return byte_;
	}

	public byte[] read(int start) throws Exception {
		byte[] byte_ = read(start, msg.length);
		return byte_;
	}

	public String seqReadString(int len) throws Exception {
		return new String(seqRead(len), DEFAULT_CHARSET);
	}

	public String seqReadString(int len, String charsetName) throws Exception {
		return new String(seqRead(len), charsetName);
	}

	public <T> T seqRead(int len, ByteArrayConverter<T> converter)
			throws Exception {
		byte[] byte_ = read(rpos, rpos + len);
		rpos += len;
		return converter.convert(byte_);
	}
}
