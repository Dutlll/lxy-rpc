package com.lxy.rpc.handler;

import com.lxy.rpc.utils.ByteBufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DebugMessageHandler extends MessageToMessageCodec<ByteBuf,ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List list) throws Exception {
        System.out.println("11");
        log.debug("aaa");
        ByteBufferUtil.debugAll(buf.nioBuffer());
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List list) throws Exception {
        System.out.println("11222222222222");
        log.debug("aaa222222222");
        ByteBufferUtil.debugAll(buf.nioBuffer());

    }
}
