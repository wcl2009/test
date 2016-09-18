package cn.wcl.test.netty.convertor;

public interface ByteArrayConverter<T> {

	public T convert(byte[] msg) throws Exception;
}
