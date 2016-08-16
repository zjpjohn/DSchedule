package com.zjp.schedule.transport;

import com.zjp.schedule.core.*;
import com.zjp.schedule.entity.Request;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
 * Date: 2016/8/9
 * Time: 10:12
 */

public class NettyRpcAccessor implements RpcAccessor {

    private static final Logger log = LoggerFactory.getLogger(NettyRpcAccessor.class);

    private String host;

    private int port;

    private RpcProcessor rpcProcessor;

    private Exporter exporter;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    public RpcProcessor getRpcProcessor() {
        return rpcProcessor;
    }

    public void setRpcProcessor(RpcProcessor rpcProcessor) {
        this.rpcProcessor = rpcProcessor;
    }

    public Exporter getExporter() {
        return exporter;
    }

    public void setExporter(Exporter exporter) {
        this.exporter = exporter;
    }

    /**
     * 初始化服务器
     *
     * @throws IOException
     */
    public void init() throws IOException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioSctpServerChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(
                                    new Encoder(),
                                    new Decoder(Request.class),
                                    new AccessHandler(NettyRpcAccessor.this));
                        }
                    });
            bootstrap.bind(host, port).sync();
        } catch (InterruptedException e) {
            log.error("start the server error:{}", e);
        }
    }

    /**
     * 设置ip
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
     * 启动监听
     *
     * @throws IOException
     */
    public void start() throws IOException {
        init();
    }

    /**
     * 停止监听
     *
     * @throws IOException
     */
    public void stop() throws IOException {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    /**
     * 设置请求处理
     *
     * @param processor
     */
    public void setProcessor(RpcProcessor processor) {
        this.rpcProcessor = processor;
    }
}
