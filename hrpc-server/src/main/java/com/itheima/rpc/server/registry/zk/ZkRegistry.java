package com.itheima.rpc.server.registry.zk;

import com.google.common.collect.Maps;
import com.itheima.rpc.annotation.HrpcService;
import com.itheima.rpc.server.config.RpcServerConfiguration;
import com.itheima.rpc.server.registry.RpcRegistry;
import com.itheima.rpc.spring.SpringBeanFactory;
import com.itheima.rpc.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author: xuebin
 * @description
 * @Date: 2023/4/4 16:50
 */
@Component
@Slf4j
public class ZkRegistry implements RpcRegistry {

    @Resource
    private ServerZKit serverZKit;

    @Resource
    private RpcServerConfiguration rpcServerConfiguration;

    @Resource
    private SpringBeanFactory springBeanFactory;

    @Override
    public void serviceRegistry() {
        /**
         * 真正完成服务注册
         */
        //找到所有标注@HrpcService 注解的 bean,将服务信息写入zk中
        Map<String, Object> beanListByAnnotationClass = springBeanFactory.getBeanListByAnnotationClass(HrpcService.class);
        if (MapUtils.isNotEmpty(beanListByAnnotationClass)) {
            //在zk中创建根节点
            serverZKit.createRootNode();

            String ip = IpUtil.getRealIp();

            for (Object bean : beanListByAnnotationClass.values()) {
                //获取bean上的@HrpcService
                HrpcService annotation = bean.getClass().getAnnotation(HrpcService.class);
                //获取接口信息
                Class<?> interfaceClass = annotation.interfaceClass();
                //创建接口目录
                String serviceName = interfaceClass.getName();
                serverZKit.createPersistentNode(serviceName);

                //在该接口下创建临时节点信息 ip:port
                String node = ip + ":" + rpcServerConfiguration.getRpcPort();
                serverZKit.createNode(serviceName + "/" + node);
                log.info("服务{}-{}注册成功", serviceName, node);

            }
        }


    }
}