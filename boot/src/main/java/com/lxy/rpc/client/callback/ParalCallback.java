package com.lxy.rpc.client.callback;

import com.lxy.rpc.bean.RpcResponseMessage;
import com.lxy.rpc.callbacker.RPCRequestCallback;
import com.lxy.rpc.client.handler.RpcResponseMessageHandler;
import com.lxy.rpc.exception.LXYRouteServiceException;
import com.lxy.rpc.exception.LXYRpcRuntimeException;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ConcurrentHashMap;

public class ParalCallback implements  RPCCallbackInterface{
    private ConcurrentHashMap<RPCRequestCallback, RpcResponseMessage> promisePool = new ConcurrentHashMap();

    public RPCRequestCallback rpcCallback(RPCRequestCallback requestCallback) {
        try {
            DefaultPromise<Object> promise = new DefaultPromise<>(
                    requestCallback.getTransmitter().channel.eventLoop()
            );
            RpcResponseMessageHandler.PROMISES.put(requestCallback.getId(), promise);
            return requestCallback;
            /**
             * 异步调用，来到这里的为调用放线程，结果之后在池里面异步调用
             */
        }catch (Exception e){
            e.printStackTrace();
            throw new LXYRpcRuntimeException(e.getMessage());
        }
    }
}
