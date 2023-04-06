package com.itheima.rpc.netty.handler;

import com.itheima.rpc.data.RpcRequest;
import com.itheima.rpc.data.RpcResponse;
import com.itheima.rpc.spring.SpringBeanFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: xuebin
 * @description
 * @Date: 2023/4/6 16:12
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        log.info("rpc 服务端收到的请求时：{}", request);
        //构造相应对象
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            //获取请求的相关数据
            String className = request.getClassName();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            //开始调用目标对象和方法
            Object targetBean = SpringBeanFactory.getBean(Class.forName(className));
            //找到目标方法
            Method targetMethod = targetBean.getClass().getMethod(methodName, parameterTypes);
            //业务执行结果
            Object result = targetMethod.invoke(targetBean, parameters);
            //结果封装
            response.setResult(result);
        } catch (Throwable e) {
            response.setCause(e);
            log.error("rpc server invoke error,msg= {}", e.getMessage());
        } finally {
            log.info("服务端执行成功，响应为:{}", response);
            ctx.channel().writeAndFlush(response);
        }
    }
}