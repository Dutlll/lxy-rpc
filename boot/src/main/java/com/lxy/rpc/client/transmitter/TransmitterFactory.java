package com.lxy.rpc.client.transmitter;

//import cn.itcast.client.handler.RpcResponseMessageHandler;
//import cn.itcast.protocol.MessageCodecSharable;
//import cn.itcast.protocol.ProcotolFrameDecoder;
import com.lxy.rpc.client.handler.RpcResponseMessageHandler;
import com.lxy.rpc.client.servicediscover.guider.IPGuider;
import com.lxy.rpc.client.servicediscover.guider.ServiceDiscoverMode;
import com.lxy.rpc.exception.LXYTimeOutException;
import com.lxy.rpc.protocol.MessageCodecSharable;
import com.lxy.rpc.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * this class is the util for create transmitter
 */
@Component
public class TransmitterFactory {

    @Autowired
    private ServiceDiscoverMode serviceDiscoverMode;

    private Channel getChannel(IPGuider ipGuider){
        Channel channel = null;
        // 初始化 channel 方法
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect(ipGuider.getIp(), Integer.valueOf(ipGuider.getPort())).sync().channel();
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");
            System.out.println("shtdonw====================");

            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            System.out.println("client error");
        }
        if (channel == null) {
            //尝试重连
            throw new LXYTimeOutException("无法连接到目标主机:"+ipGuider.toString());
        }
        return channel;
    }

    private Channel getChannelRetry(IPGuider ipGuider){
        Channel channel = null;
        try{
            channel = getChannel(ipGuider);
        }catch (Exception e){
            try {
                channel = getChannel(ipGuider);
            }catch (Exception ee){
                throw new LXYTimeOutException("无法连接到目标主机"+ipGuider.toString());
            }
        }
        return channel;
    }

    public AbstractTransmitter build(IPGuider ipGuider) throws LXYTimeOutException{
        Channel channel = null;
        try {
            channel = getChannelRetry(ipGuider);
        }catch (LXYTimeOutException e) {
            /**
             * we should try again,to reduce to problem of notwork buzy,
             * and then if also fail, throw error, let upper try get ip again
             * and try agin, if alse fail,maybe really fail, should throw error
             */
            /**
             * 因此，这个类要使用服务注册中心获取的bean，来进行重试操作
             */
            try {
                ipGuider = serviceDiscoverMode.getServiceDiscover().getIPGuider(ipGuider.toString());
            }catch (Exception eee){
                System.out.println("无法获取目标服务的地址："+ipGuider.toString());
                throw new LXYTimeOutException("无法获取目标服务的地址："+ipGuider.toString());
            }
        }
        if (channel == null){
            channel = getChannelRetry(ipGuider);
        }
        return new LongConnectTransmitter(channel);
    }
}
