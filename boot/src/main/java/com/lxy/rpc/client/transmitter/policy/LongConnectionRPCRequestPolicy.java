package com.lxy.rpc.client.transmitter.policy;

//import cn.itcast.message.RpcRequestMessage;
import com.lxy.rpc.bean.RpcRequestMessage;
import com.lxy.rpc.callbacker.RPCRequestCallback;
import com.lxy.rpc.client.servicediscover.guider.AbstractServiceDiscover;
import com.lxy.rpc.client.servicediscover.guider.IPGuider;
import com.lxy.rpc.client.servicediscover.guider.ServiceDiscoverMode;
import com.lxy.rpc.client.transmitter.AbstractTransmitter;
import com.lxy.rpc.client.transmitter.LongConnectTransmitterPool;
import com.lxy.rpc.client.transmitter.TransmitterFactory;
import com.lxy.rpc.exception.LXYTimeOutException;

/**
 * 作为一种发送策略：
 * 这种长连接的：
 * 1，需要长连接的池，
 *
 */
public class LongConnectionRPCRequestPolicy implements RPCRequestPolicy {

    private LongConnectTransmitterPool longConnectTransmitterPool;

    private ServiceDiscoverMode serviceDiscoverMode;

    private TransmitterFactory transmitterFactory;

    public LongConnectTransmitterPool getLongConnectTransmitterPool() {
        return longConnectTransmitterPool;
    }

    public void setLongConnectTransmitterPool(LongConnectTransmitterPool longConnectTransmitterPool) {
        this.longConnectTransmitterPool = longConnectTransmitterPool;
    }

    public ServiceDiscoverMode getServiceDiscoverMode() {
        return serviceDiscoverMode;
    }

    public void setServiceDiscoverMode(ServiceDiscoverMode serviceDiscoverMode) {
        this.serviceDiscoverMode = serviceDiscoverMode;
    }

    public TransmitterFactory getTransmitterFactory() {
        return transmitterFactory;
    }

    public void setTransmitterFactory(TransmitterFactory transmitterFactory) {
        this.transmitterFactory = transmitterFactory;
    }

    @Override
    public RPCRequestCallback send(RpcRequestMessage rpcRequestMessage) {
        AbstractTransmitter transmitter = longConnectTransmitterPool.getTransmitter(rpcRequestMessage.getServiceName());

        System.out.println("-------");
        System.out.println(transmitter);
        if (transmitter == null || transmitter.isAvailable.get() == false){
            AbstractServiceDiscover serviceDiscover = serviceDiscoverMode.getServiceDiscover();
            IPGuider ipGuider = serviceDiscover.getIPGuider(rpcRequestMessage.getServiceName());
            if (ipGuider == null) throw new LXYTimeOutException("无法获取远程服务ip地址:"+rpcRequestMessage.getServiceName());
            AbstractTransmitter build = transmitterFactory.build(ipGuider);
            if (build == null) throw new LXYTimeOutException("无法连接到远程服务器获取服务"+ipGuider.toString());
            longConnectTransmitterPool.updateTransmitter(rpcRequestMessage.getServiceName(),build);

            longConnectTransmitterPool.updateTransmitter(rpcRequestMessage.getServiceName(),build);
            transmitter = build;
        }
        transmitter.doReqForRPC(rpcRequestMessage);
        //参数封装返回，回调请求
        return new RPCRequestCallback(rpcRequestMessage.getSequenceId(), transmitter);
    }

}
