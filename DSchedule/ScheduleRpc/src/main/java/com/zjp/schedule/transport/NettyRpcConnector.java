package com.zjp.schedule.transport;

import com.zjp.schedule.client.ConnectionHandler;
import com.zjp.schedule.client.RpcFuture;
import com.zjp.schedule.client.RpcFutureUtil;
import com.zjp.schedule.core.Config;
import com.zjp.schedule.core.Decoder;
import com.zjp.schedule.core.Encoder;
import com.zjp.schedule.core.RpcConnector;
import com.zjp.schedule.entity.Request;
import com.zjp.schedule.entity.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * ━━━━━━南无阿弥陀佛━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃stay hungry stay foolish
 * 　　　　┃　　　┃Code is far away from bug with the animal protecting
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━萌萌哒━━━━━━
 * Module Desc:com.zjp.schedule.transport
 * User: zjprevenge
 * Date: 2016/8/11
 * Time: 18:04
 */

public class NettyRpcConnector implements RpcConnector {

    private static final Logger log = LoggerFactory.getLogger(NettyRpcAccessor.class);

    private String host;

    private int port;

    private Channel channel;

    private RpcFutureUtil futureUtil = new RpcFutureUtil();

    private EventLoopGroup group;

    public void init() {
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(
                                    new Encoder(),
                                    new Decoder(Response.class),
                                    new ConnectionHandler(futureUtil));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channel = channelFuture.channel();
        } catch (Exception e) {
            log.error("init connection error:{}", e);
        }
    }

    /**
     * 请求rpc服务
     *
     * @param request
     * @return
     * @throws IOException
     */
    public Response invoke(Request request) throws IOException {
        long id = request.getRequestId();
        RpcFuture<Object> future = new RpcFuture<Object>();
        try {
            futureUtil.setRpcFuture(id, future);
            channel.writeAndFlush(request);
            future.await(Config.RPC_TIMEOUT, TimeUnit.MILLISECONDS);
            return future.getResponse();
        } catch (InterruptedException e) {
            log.error("InterruptedException:{}", e);
        } catch (ExecutionException e) {
            log.error("ExecutionException:{}", e);
        }
        return null;
    }

    /**
     * 设置host
     *
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 设置端口
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 启动
     *
     * @throws IOException
     */
    public void start() throws IOException {
        init();
    }

    /**
     * 停止
     *
     * @throws IOException
     */
    public void stop() throws IOException {
        group.shutdownGracefully();
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
