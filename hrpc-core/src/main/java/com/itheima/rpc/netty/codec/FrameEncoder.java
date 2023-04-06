package com.itheima.rpc.netty.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author: xuebin
 * @description
 * @Date: 2023/4/6 15:51
 */
public class FrameEncoder extends LengthFieldPrepender {
    public FrameEncoder() {
        super(2);
    }
}