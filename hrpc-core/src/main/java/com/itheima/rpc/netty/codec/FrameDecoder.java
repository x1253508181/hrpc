package com.itheima.rpc.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author: xuebin
 * @description
 * @Date: 2023/4/6 15:49
 */
public class FrameDecoder extends LengthFieldBasedFrameDecoder {
    public FrameDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}