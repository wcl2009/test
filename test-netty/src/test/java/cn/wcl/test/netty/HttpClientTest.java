package cn.wcl.test.netty;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import cn.wcl.test.netty.utils.ByteConvertUtil;

public class HttpClientTest {

	public static int i = 0;
	// 读取超时
	private final static int SOCKET_TIMEOUT = 100000;
	// 连接超时
	private final static int CONNECTION_TIMEOUT = 100000;
	// 每个HOST的最大连接数量
	private final static int MAX_CONN_PRE_HOST = 100;
	// 连接池的最大连接数
	private final static int MAX_CONN = 500;

	public static void main(String[] args) throws IOException {
		final MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = httpConnectionManager.getParams();
		params.setConnectionTimeout(CONNECTION_TIMEOUT);
		params.setSoTimeout(SOCKET_TIMEOUT);
		params.setDefaultMaxConnectionsPerHost(MAX_CONN_PRE_HOST);
		params.setMaxTotalConnections(MAX_CONN);
		Long start = System.currentTimeMillis();

		try {
			for (int i = 0; i < 10000; i++) {
				Thread thread = new Thread() {
					public void run() {
						PostMethod postMethod = new PostMethod(
								"http://127.0.0.1:8844/UPLOAD");
						HttpClient httpClient = new HttpClient(
								httpConnectionManager);
						try {
							long start = System.currentTimeMillis();
							// byte[] val = readFile("d://test/test.zip");
							byte[] val = getByte();
							System.out.println(getByte().length);
							// System.out.println("######send:"
							// + Arrays.toString(val));
							InputStream in = new ByteArrayInputStream(val);
							postMethod.setRequestBody(in);
							HttpClientParams params = new HttpClientParams();
							httpClient.setParams(params);
							httpClient.executeMethod(postMethod);
							// 获取二进制的byte流
							long end = System.currentTimeMillis();
							String response = postMethod
									.getResponseBodyAsString();
							if (response.indexOf("0000") < 0) {
								System.out.println(response);
								System.out.println(end - start);
							}
							// if (!val_[0].equals("0000"))
							// System.out.println(Arrays.toString(val));
							// System.out.println("client:" + str);
							// HttpClientTest.i++;
							// System.out.println(HttpClientTest.i++);
							// latch.countDown();
						} catch (HttpException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							postMethod.releaseConnection();
							httpClient.getHttpConnectionManager()
									.closeIdleConnections(0);
						}
					};

				};
				thread.start();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage() + "," + e.getStackTrace());
		} finally {
		}
	}

	/**
	 * header 文件名长度：4 数据长度：4 类型：3
	 * 
	 * content 文件 MD5：32位
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private static byte[] readFile(String filePath) throws IOException {
		byte[] body;
		byte[] total;
		FileInputStream fileInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		BufferedOutputStream bos = null;
		byte[] total_;
		try {
			File file = new File(filePath);
			String fileName = file.getName();
			// System.out.println(fileName);
			byte[] fileName_ = fileName.getBytes();
			byte[] len_fileName = ByteConvertUtil.int2byte(fileName_.length);
			// System.out.println("len_fileName:" +
			// Arrays.toString(len_fileName));

			fileInputStream = new FileInputStream(file);
			byteArrayOutputStream = new ByteArrayOutputStream();
			int i;
			// 转化为字节数组流
			while ((i = fileInputStream.read()) != -1) {
				byteArrayOutputStream.write(i);
			}
			byte[] filea = byteArrayOutputStream.toByteArray();
			byte[] md5 = DigestUtils.md5Hex(filea).getBytes();
			// System.out.println("md5:" + md5);
			// System.out.println("md5:" + md5.length);

			byte[] len_content = ByteConvertUtil.int2byte(filea.length);
			// System.out.println("len_content:" +
			// Arrays.toString(len_content));

			String type = "001";
			// System.out.println("type:" + Arrays.toString(type.getBytes()));
			// 包头11位
			byte[] header = new byte[11];
			System.arraycopy(len_fileName, 0, header, 0, 4);
			System.arraycopy(len_content, 0, header, 4, 4);
			System.arraycopy(type.getBytes(), 0, header, 8, 3);
			// System.out.println(Arrays.toString(header));
			// System.out.println("body len:" + (fileName_.length +
			// filea.length));
			body = new byte[fileName_.length + filea.length + md5.length];
			System.arraycopy(fileName_, 0, body, 0, fileName_.length);
			// System.out.println("file:"+Arrays.toString(filea));
			System.arraycopy(filea, 0, body, fileName_.length, filea.length);
			System.arraycopy(md5, 0, body, filea.length + fileName_.length,
					md5.length);
			// System.out.println("body:" + Arrays.toString(body));
			// System.out.println(body.length);

			total = new byte[header.length + body.length];
			System.arraycopy(header, 0, total, 0, header.length);
			System.arraycopy(body, 0, total, header.length, body.length);
			total_ = new byte[total.length + 4];
			byte[] tot_len = ByteConvertUtil.int2byte(total.length + 4);
			System.arraycopy(tot_len, 0, total_, 0, 4);
			System.arraycopy(total, 0, total_, 4, total.length);
			System.out.println(Arrays.toString(total_));
		} finally {
			fileInputStream.close();
			byteArrayOutputStream.close();
		}
		return total_;
	}

	private static byte[] getByte() {
		byte[] val = { 123, 6, 0, 0, 8, 0, 0, 0, 68, 6, 0, 0, 48, 48, 49, 116,
				101, 115, 116, 46, 122, 105, 112, 80, 75, 3, 4, 20, 0, 0, 0, 8,
				0, -59, 72, 26, 73, -113, -105, 101, -60, -84, 5, 0, 0, 112,
				29, 6, 0, 9, 0, 0, 0, 116, 101, 115, 116, 49, 46, 116, 120,
				116, -19, -52, -95, 10, 2, 65, 20, -123, -31, 46, -8, -2, 89,
				-63, 110, -72, -85, 47, -80, -32, 48, -62, 6, -125, 89, 16,
				-77, -80, 23, 22, -117, -96, -45, 124, -128, 73, -14, -123,
				-45, -2, -13, -43, 83, -28, -31, 86, -33, -111, -111, -11, -15,
				-69, 97, 44, -81, 120, 78, -105, -78, 43, -53, 113, 27, 89,
				-106, 122, 109, 85, -28, 48, -18, 55, -33, -33, 124, -66, -57,
				-68, 94, 17, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -31, 31, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, -102, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 66, 19, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 104, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, -67, 5, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4,
				2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127,
				64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32,
				16, 8, 4, 2, -127, 64, 32, 16, 122, 11, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, -12, 22, 8,
				4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64,
				32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16,
				8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2,
				-127, 64, -24, 45, 124, 0, 80, 75, 1, 2, 31, 0, 20, 0, 0, 0, 8,
				0, -59, 72, 26, 73, -113, -105, 101, -60, -84, 5, 0, 0, 112,
				29, 6, 0, 9, 0, 36, 0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0,
				0, 116, 101, 115, 116, 49, 46, 116, 120, 116, 10, 0, 32, 0, 0,
				0, 0, 0, 1, 0, 24, 0, 1, 82, -96, 8, 54, -1, -47, 1, 107, 50,
				-93, 106, 53, -1, -47, 1, -128, 81, 24, 71, 53, -1, -47, 1, 80,
				75, 5, 6, 0, 0, 0, 0, 1, 0, 1, 0, 91, 0, 0, 0, -45, 5, 0, 0, 0,
				0, 52, 54, 57, 55, 56, 98, 101, 102, 52, 102, 99, 55, 55, 99,
				100, 53, 56, 98, 98, 51, 100, 50, 54, 53, 102, 50, 51, 51, 57,
				100, 98, 53 };
		return val;
		
	}

}
