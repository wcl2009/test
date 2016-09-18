package cn.wcl.test.netty;

import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import cn.wcl.test.netty.constants.UploadProtocalConstant;

public class ConstantProtocalHeaderReader extends ProtocalReaderAbstract {

	public ConstantProtocalHeaderReader(byte[] msg) {
		super(msg);
	}

	public Map<?, ?> readHeader() throws Exception {
		LinkedMap protocalRule = new LinkedMap();
		protocalRule.put(UploadProtocalConstant.Header.FILE_NAME,
				UploadProtocalConstant.Header.FILE_NAME.getLen());
		protocalRule.put(UploadProtocalConstant.Header.DATA,
				UploadProtocalConstant.Header.DATA.getLen());
		protocalRule.put(UploadProtocalConstant.Header.DATA_TYPE,
				UploadProtocalConstant.Header.DATA_TYPE.getLen());
		Map<?, ?> res = readMap(protocalRule);
		return res;
	}

	@Override
	protected void bind() {
		this.bindConverter(DefalutConverter.INTEGER)
				.bindConverter(DefalutConverter.INTEGER)
				.bindConverter(DefalutConverter.STR_UTF8);
	}

}
