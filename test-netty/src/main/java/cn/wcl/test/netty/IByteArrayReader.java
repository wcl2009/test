package cn.wcl.test.netty;

public interface IByteArrayReader {

	public byte[] read(int start, int end) throws Exception;
	
	public void release() throws Exception;
}
