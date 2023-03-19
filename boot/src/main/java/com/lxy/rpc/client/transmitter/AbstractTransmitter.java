package com.lxy.rpc.client.transmitter;

//import cn.itcast.message.RpcRequestMessage;
import com.lxy.rpc.bean.RpcRequestMessage;
import com.lxy.rpc.config.LXYAbstractConfig;
import com.lxy.rpc.exception.LXYTimeOutException;
import io.netty.channel.Channel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * for Channel , we could get Channel by this class ,and then ,we could send message
 * for RPC
 */
public abstract class AbstractTransmitter {

    public volatile AtomicBoolean isAvailable = new AtomicBoolean(true);

    private LXYAbstractConfig lxyAbstractConfig;

    public abstract void doReqForRPC(RpcRequestMessage rpcRequestMessage);

    public Channel channel;

    public AbstractTransmitter(Channel channel){
        this.channel = channel;
    }

    public void closeChannel(){
        channel.close();
    }

    /**
     * heart beat detection,if throw Exception means there is unavailable to tar service
     * @throws LXYTimeOutException
     */
    public abstract void ping() throws LXYTimeOutException;

}
