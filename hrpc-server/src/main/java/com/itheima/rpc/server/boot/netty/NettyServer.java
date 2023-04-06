package com.itheima.rpc.server.boot.netty;

import com.itheima.rpc.netty.codec.FrameDecoder;
import com.itheima.rpc.netty.codec.FrameEncoder;
import com.itheima.rpc.netty.codec.RpcRequestDecoder;
import com.itheima.rpc.netty.codec.RpcResponseEncoder;
import com.itheima.rpc.netty.handler.RpcRequestHandler;
import com.itheima.rpc.server.boot.RpcServer;
import com.itheima.rpc.server.config.RpcServerConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: xuebin
 * @description
 * @Date: 2023/4/6 15:29
 */
@Component
@Slf4j
public class NettyServer implements RpcServer {

    @Autowired
    private RpcServerConfiguration rpcServerConfiguration;

    @Override
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("boos"));
        EventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));
        UnorderedThreadPoolEventExecutor business = new UnorderedThreadPoolEventExecutor(NettyRuntime.availableProcessors() * 2, new DefaultThreadFactory("business"));

        RpcRequestHandler rpcRequestHandler = new RpcRequestHandler();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("logHandler",new LoggingHandler(LogLevel.INFO));
                            pipeline.addLast("FrameEncoder",new FrameEncoder());
                            pipeline.addLast("RpcResponseEncoder",new RpcResponseEncoder());
                            pipeline.addLast("RpcRequestDecoder",new RpcRequestDecoder());
                            pipeline.addLast(business,"RpcRequestHandler",rpcRequestHandler);                        }
                    });
            ChannelFuture future = serverBootstrap.bind(rpcServerConfiguration.getRpcPort()).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("rpc server start error msg = {}", e.getMessage());
        } finally {
            business.shutdownGracefully();
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
     }
}