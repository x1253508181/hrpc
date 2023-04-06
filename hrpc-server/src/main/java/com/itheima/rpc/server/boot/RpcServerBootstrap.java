package com.itheima.rpc.server.boot;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author: xuebin
 * @description
 * @Date: 2023/4/4 16:40
 */
@Configuration
//@DependsOn("springBeanFactory")
public class RpcServerBootstrap {

    @Resource
    private RpcServerRunner rpcServerRunner;

    @PostConstruct
    public void initRpcServer() {
        //引导rpc server的启动
        rpcServerRunner.run();
    }
}