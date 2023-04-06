package com.itheima.rpc.netty.codec;

import com.itheima.rpc.data.RpcRequest;
import com.itheima.rpc.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: xuebin
 * @description
 * @Date: 2023/4/6 15:56
 */
@Slf4j
public class RpcRequestDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            int i = msg.readableBytes();
            byte[] bytes = new byte[i];
            msg.readBytes(bytes);
            RpcRequest request = ProtostuffUtil.deserialize(bytes, RpcRequest.class);
            out.add(request);
        } catch (Exception e) {
            log.error("RpcRequestDecoder decode error,msg={}", e.getMessage());
            throw new RuntimeException();
        }

    }
}