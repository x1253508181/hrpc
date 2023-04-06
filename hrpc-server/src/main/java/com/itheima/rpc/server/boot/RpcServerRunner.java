package com.itheima.rpc.server.boot;

import com.itheima.rpc.server.registry.RpcRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: xuebin
 * @description
 * @Date: 2023/4/4 16:43
 */
@Component
public class RpcServerRunner {

    @Resource
    private RpcRegistry rpcRegistry;

    /**
     * 启动 rpc server
     */
    public void run() {
        //1、服务注册
        rpcRegistry.serviceRegistry();

        //2、基于netty绑定端口，启动服务，监听连接，接受链接处理数据

    }
}