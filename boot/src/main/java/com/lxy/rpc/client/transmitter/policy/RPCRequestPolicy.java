package com.lxy.rpc.client.transmitter.policy;

//import cn.itcast.message.PingMessage;
//import cn.itcast.message.RpcRequestMessage;
import com.lxy.rpc.bean.RpcRequestMessage;
import com.lxy.rpc.callbacker.RPCRequestCallback;

public interface RPCRequestPolicy {
    public RPCRequestCallback send(RpcRequestMessage rpcRequestMessage);
}
