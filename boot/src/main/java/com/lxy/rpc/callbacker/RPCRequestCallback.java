package com.lxy.rpc.callbacker;

import com.lxy.rpc.client.transmitter.AbstractTransmitter;
import com.lxy.rpc.bean.RpcResponseMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * 这个类封装了，必要的回调如channel，id号等等可以用作id
 */

@Getter
@Setter
public class RPCRequestCallback {
    private int id;
    private AbstractTransmitter transmitter;
    private RpcResponseMessage rpcResponseMessage;
    public RPCRequestCallback(int id, AbstractTransmitter transmitter) {
        this.id = id;
        this.transmitter = transmitter;
    }
}
