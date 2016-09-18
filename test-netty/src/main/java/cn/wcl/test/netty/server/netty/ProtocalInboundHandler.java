package cn.wcl.test.netty.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.map.LinkedMap;
import org.springframework.context.ApplicationContext;

import cn.wcl.test.netty.ConstantProtocalDataReader;
import cn.wcl.test.netty.ConstantProtocalHeaderReader;
import cn.wcl.test.netty.constants.RtCode;
import cn.wcl.test.netty.constants.UploadProtocalConstant;
import cn.wcl.test.netty.convertor.ByteToIntegerConverter;
import cn.wcl.test.netty.server.ProtocalHandler;

/**
 * 协议输入解析处理器，用来解析协议报文并对报文内容进行业务处理，返回操作结果
 * 
 * @author wangcl
 */
public class ProtocalInboundHandler extends HttpServerInboundHandler {

	private HttpRequest request;

	// 已读取信息长度
	private int dataReadLength = 0;

	// 临时存储对象
	private ByteBuf tempBuf;

	protected String ip;

	// 完整数据长度
	private int data_len = -1;

	protected String uri;

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		if (tempBuf != null) {
			tempBuf.release();
			tempBuf = null;
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		super.channelRead(ctx, msg);
		if (msg instanceof HttpRequest) {
			request = (HttpRequest) msg;
		}
		if (msg instanceof HttpContent) {
			HttpContent content = (HttpContent) msg;
			ByteBuf buf = content.content();
			// 读取块数据
			int len = buf.readableBytes();
			if (tempBuf == null) {
				tempBuf = ctx.alloc().buffer(buf.readableBytes());
				tempBuf.writeBytes(buf);
			} else {
				tempBuf.writeBytes(buf);
			}
			((HttpContent) msg).release();
			// 上传请求
			// 累加读取长度
			dataReadLength += len;
			// 如果当前读取长度小于4（不足以读取报文长度），则继续读取数据
			if (data_len == -1 && dataReadLength >= 4) {
				// 声明一个长度为4的byte数组来保存包头长度
				byte[] data_len_ = new byte[4];
				// 读取报文长度（前4位byte数组值所对应的int值）
				tempBuf.readBytes(data_len_, 0, 4);
				// 转化报文总长度数组为int值
				data_len = new ByteToIntegerConverter().convert(data_len_);
			}
			// 读取长度小于报文长度或者报文长度尚未初始化时，继续读取
			if (dataReadLength < data_len || data_len == -1) {
				return;
			}
			// 装载报文（去掉包长长度）
			byte[] bytes = new byte[data_len - 4];
			tempBuf.readBytes(bytes);
			tempBuf.release();
			tempBuf = null;
			// 解析报文
			RtCode code = decodeDataByProtocal(bytes);
			// 释放内存
			dataReadLength = 0;
			data_len = -1;
			writeResponse(ctx.channel(), code);
		}
	}

	public ProtocalInboundHandler(ApplicationContext applicationContext,
			boolean needlogin) {
		if (super.applicationContext == null) {
			setApplicationContext(applicationContext);
		}
		if (super.needLogin == null)
			super.needLogin = needlogin;
	}

	@Override
	protected final RtCode decodeDataByProtocal(byte[] bytes) throws Exception {
		String type;
		// 生成包头解析对象
		ConstantProtocalHeaderReader reader = new ConstantProtocalHeaderReader(
				bytes);
		// 读取包头元素
		Map<?, ?> res = reader.readHeader();
		// 获取协议类型
		type = res.get(UploadProtocalConstant.Header.DATA_TYPE).toString();
		LinkedMap protocalRule = new LinkedMap();
		// 构造包体解析规则，包括文件名长度，文件长度，MD5长度
		protocalRule.put("fileName",
				res.get(UploadProtocalConstant.Header.FILE_NAME));
		protocalRule.put("file", res.get(UploadProtocalConstant.Header.DATA));
		protocalRule.put("md5", UploadProtocalConstant.Data.MD5.getLen());
		// 读取包体内容

		// 获取文件byte数组
		byte[] file_byte = null;
		try {
			res = new ConstantProtocalDataReader(reader).readMap(protocalRule);
			file_byte = (byte[]) res.get("file");
		} catch (Exception e) {
			System.out.println(res.get("file"));
			e.printStackTrace();
			throw e;
		}
		// 获取MD5
		String md5 = res.get("md5").toString();
		// 验证文件完整性
		if (!Md5Util.validate(file_byte, md5)) {
			return RtCode.ERROR_MD5;
		}
		// 获取文件名
		String fileName = res.get("fileName").toString();
		// 解压ZIP文件
		Map<String, List<String>> file = UnzipUtil.unzip((byte[]) res
				.get("file"));
		// // 获取文件处理器
		ProtocalHandler handler = (ProtocalHandler) applicationContext
				.getBean("protocalHandler" + type);
		// 处理文件内容并返回
		RtCode val = handler.handle(type, fileName, file);
		logger.info("ip:" + ip + ",filename:" + fileName + ",rtCode:"
				+ val.code);
		return val;
	}

	/**
	 * 构建返回消息
	 * 
	 * @param code
	 *            返回操作结果码
	 * @return
	 */
	private void writeResponse(Channel channel, RtCode success) {
		String val = success.code + ":" + success.msg;
		boolean close = request.headers().contains(HttpHeaderNames.CONNECTION,
				HttpHeaderValues.CLOSE, true)
				|| request.protocolVersion().equals(HttpVersion.HTTP_1_0)
				&& !request.headers().contains(HttpHeaderNames.CONNECTION,
						HttpHeaderValues.KEEP_ALIVE, true);

		// Build the response object.
		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.wrappedBuffer(val.getBytes()));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE,
				"text/plain; charset=UTF-8");
		if (!close) {
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,
					val.getBytes().length);
		}
		ChannelFuture future = channel.writeAndFlush(response);
		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.error("数据接收处理异常", cause);
		if (tempBuf != null) {
			tempBuf.release();
			tempBuf = null;
		}
		ctx.channel().close();
	}

	/**
	 * MD5验证工具
	 * 
	 * @author wangcl
	 */
	private static class Md5Util {

		/**
		 * 比较文件MD5生成值与传输值是否一致
		 * 
		 * @param src
		 *            文件byte数组
		 * @param tar
		 *            MD5
		 * @return
		 */
		public static boolean validate(byte[] src, String tar) {
			if (src == null || src.length == 0 || tar == null || tar.equals(""))
				return false;
			return DigestUtils.md5Hex(src).equals(tar);
		}
	}

	/**
	 * ZIP压缩数据流文件解析工具类
	 * 
	 * @author wangcl 2016-8-24
	 */
	private static class UnzipUtil {

		/**
		 * 解压ZIP文件流
		 * 
		 * @param file
		 *            ZIP压缩格式byte数组
		 * @return Map 解压文件:key 文件名;value:文件内容
		 * @throws Exception
		 */
		public static Map<String, List<String>> unzip(byte[] file)
				throws Exception {

			ZipInputStream zin = null;
			ZipEntry entry = null;
			BufferedReader br = null;
			String line;
			String[] folder;
			String name;
			// 初始化结果集
			HashMap<String, List<String>> res = new HashMap<String, List<String>>();
			List<String> content;
			try {
				// 构造ZIP流
				zin = new ZipInputStream(new ByteArrayInputStream(file));
				// 遍历zip文件
				while ((entry = zin.getNextEntry()) != null) {
					// 如果是文件夹则不处理
					if (entry.isDirectory() || entry.getName().equals("..\\"))
						continue;
					// 构造文件内容结果集
					content = new ArrayList<String>();
					// 构造文件读取流
					br = new BufferedReader(new InputStreamReader(zin, "utf-8"));

					// 获取文件名
					folder = entry.getName().split("/");
					name = folder[folder.length - 1];
					// 读取文件
					while ((line = br.readLine()) != null) {
						content.add(line);
					}
					res.put(name, content);

				}
			} catch (Exception e) {
				throw e;
			} finally {
				if (br != null) {
					br.close();
					br = null;
				}
				if (zin != null) {
					zin.close();
					zin = null;
				}
			}
			return res;
		}
	}

}
