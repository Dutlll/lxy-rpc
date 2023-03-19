package com.lxy.rpc.handler;


import com.lxy.rpc.bean.PingMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
@ChannelHandler.Sharable
public class PINGRequestMessageHandler extends SimpleChannelInboundHandler<PingMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PingMessage pingMessage) throws Exception {
        try{
            System.out.println("    protected void channelRead0(ChannelHandlerContext channelHandlerContext");
        }catch (Exception e){

        }
    }
}
