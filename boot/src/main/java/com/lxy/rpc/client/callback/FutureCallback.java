package com.lxy.rpc.client.callback;

//import cn.itcast.client.handler.RpcResponseMessageHandler;
import com.lxy.rpc.callbacker.RPCRequestCallback;
import com.lxy.rpc.bean.RpcResponseMessage;
import com.lxy.rpc.client.handler.RpcResponseMessageHandler;
import com.lxy.rpc.exception.LXYRpcRuntimeException;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ConcurrentHashMap;

public class FutureCallback implements RPCCallbackInterface{

    private ConcurrentHashMap<RPCRequestCallback, RpcResponseMessage> promisePool = new ConcurrentHashMap();

    @Override
    public RPCRequestCallback rpcCallback(RPCRequestCallback requestCallback) {
        try {
            DefaultPromise<Object> promise = new DefaultPromise<>(
                    requestCallback.getTransmitter().channel.eventLoop()
            );
            RpcResponseMessageHandler.PROMISES.put(requestCallback.getId(), promise);
//            promise.await();
//            if (promise.isSuccess()) {
//                System.out.println(promise.getNow());
//                return promise.getNow();
//            } else {
//                System.out.println("服务调用失败");
//                throw new LXYRouteServiceException("远程服务调用异常");
//            }
            return requestCallback;
        }catch (Exception e){
            throw new LXYRpcRuntimeException(e.getMessage());
        }
    }
}
