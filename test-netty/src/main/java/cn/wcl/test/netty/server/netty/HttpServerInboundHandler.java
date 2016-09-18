package cn.wcl.test.netty.server.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import cn.wcl.test.netty.constants.RtCode;
import cn.wcl.test.netty.service.LoginService;

/**
 * HTTP协议请求入口解析器
 * 
 * @author wangcl
 */
public abstract class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
    
    
    protected static final Logger logger = LoggerFactory.getLogger(HttpServerInboundHandler.class);
    
    protected ApplicationContext applicationContext;
    
    protected Boolean needLogin;
    
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取HTTP协议包头
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            String uri = request.uri();
            String ip = request.headers().get("X-Forwarded-For");
            if (ip == null) {
                InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = insocket.getAddress().getHostAddress();
            }
            LoginService loginService = (LoginService) applicationContext.getBean("loginServiceImpl");
            if ("/login".equalsIgnoreCase(uri)) {
                HashMap<String, Object> parms = new HashMap<>();
                parms.put("ip", ip);
                parms.put("time", new Date());
                loginService.login(parms);
                logger.info("ip[{}],login ", ip);
                writeResponseIntenal(ctx.channel(), request, RtCode.SUCCESS);
                return;
            }
            if ("/upload".equalsIgnoreCase(uri) && !loginService.isLogin(ip)&&needLogin) {
                logger.info("ip[{}],not login",ip);
                writeResponseIntenal(ctx.channel(), request, RtCode.ERROR_NO_LOGIN);
                return;
            }
        }
        
    }
    
    /**
     * 构建返回消息
     * 
     * @param code
     *            返回操作结果码
     * @return
     */
    private void writeResponseIntenal(Channel channel, HttpRequest request, RtCode success) {
        String val = success.code + ":" + success.msg;
        boolean close = request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE, true)
                || request.protocolVersion().equals(HttpVersion.HTTP_1_0)
                        && !request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE, true);
        
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(val.getBytes()));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        if (!close) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, val.getBytes().length);
        }
        ChannelFuture future = channel.writeAndFlush(response);
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    protected abstract RtCode decodeDataByProtocal(byte[] bytes) throws Exception;
    
}
