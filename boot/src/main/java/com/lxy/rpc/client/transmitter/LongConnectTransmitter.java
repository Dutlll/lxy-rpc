package com.lxy.rpc.client.transmitter;
//import cn.itcast.message.PingMessage;
//import cn.itcast.message.RpcRequestMessage;
//import com.lxy.rpc.bean.PingMessage;
import com.lxy.rpc.bean.PingMessage;
import com.lxy.rpc.bean.RpcRequestMessage;
import com.lxy.rpc.exception.LXYTimeOutException;
import io.netty.channel.Channel;

/**
 * this impl will maintain long connect to remote　host.
 * what more ,durring the connection , there will be heartbeat detection,
 * to encurency the connection is timeliness
 */
public class LongConnectTransmitter extends AbstractTransmitter{

    public LongConnectTransmitter(Channel channel){
        super(channel);
    }

    @Override
    public void doReqForRPC(RpcRequestMessage rpcRequestMessage) {
        this.channel.writeAndFlush(rpcRequestMessage);
    }

    @Override
    public void ping() throws LXYTimeOutException {
        try {
            if (this.isAvailable.get() == true) {
//            ChannelPromise promise = new DefaultChannelPromise(channel);

                System.out.println("----------开始发送ping请求-----------");
                System.out.println("----------开始发送ping请求-----------");
                System.out.println("----------开始发送ping请求-----------");
//            final ByteBuf buffer = channel.alloc().buffer();

                final boolean active = this.channel.isActive();
                if (active == false) {
                    this.isAvailable.compareAndSet(true, false);
                    closeChannel();
                }
                this.channel.writeAndFlush(

                        new PingMessage());
//                    new RpcRequestMessage(
//                            1,
//                            "","",Object.class,new Class[0],new Object[0])
//                    ,promise);
//            promise.sync();
                System.out.println("----------开始发送ping请求-----------");
                System.out.println("----------开始发送ping请求-----------");
                System.out.println("----------开始发送ping请求-----------");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
