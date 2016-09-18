package cn.wcl.test.netty;

public class ConstantProtocalDataReader extends ProtocalReaderAbstract {

	private ConstantProtocalDataReader(byte[] msg) {
		super(msg);
	}

	public ConstantProtocalDataReader(ConstantProtocalHeaderReader reader)
			throws Exception {
		super(reader.read(reader.readLength()));
	}

	@Override
	protected void bind() {
		bindConverter(DefalutConverter.STR_UTF8)
		.bindConverter(DefalutConverter.DEFAULT)
		.bindConverter(DefalutConverter.STR_UTF8);
	}

}
