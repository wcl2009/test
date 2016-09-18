package cn.wcl.test.netty;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpContent;

public class ByteStreamDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in instanceof HttpContent) {
			if (in.readableBytes() < 4)
				return;
			in.markReaderIndex();
			int dataLength = in.readInt();
			if (in.readableBytes() < dataLength) {
				in.resetReaderIndex();
				return;
			}
		}
		out.add(in);
	}


}
