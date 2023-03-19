package com.lxy.rpc.client.callback;

//import cn.itcast.client.handler.RpcResponseMessageHandler;
import com.lxy.rpc.callbacker.RPCRequestCallback;
import com.lxy.rpc.bean.RpcResponseMessage;
import com.lxy.rpc.client.handler.RpcResponseMessageHandler;
import com.lxy.rpc.exception.LXYRouteServiceException;
import com.lxy.rpc.exception.LXYRpcRuntimeException;
import io.netty.util.concurrent.DefaultPromise;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 同步调用，会使用channel来阻塞等待，客户端维持结果池，提升并发度
 */
public class SyncCallback implements RPCCallbackInterface{

    private ConcurrentHashMap<RPCRequestCallback, RpcResponseMessage> promisePool = new ConcurrentHashMap();

    @Override
    public RPCRequestCallback rpcCallback(RPCRequestCallback requestCallback) {
        try {
            DefaultPromise<Object> promise = new DefaultPromise<>(
                    requestCallback.getTransmitter().channel.eventLoop()
            );
            RpcResponseMessageHandler.PROMISES.put(requestCallback.getId(), promise);

            promise.await();
            if (promise.isSuccess()) {
                System.out.println(promise.getNow());
                final Object now = promise.getNow();
                System.out.println("--------------=-=-=-=d-=sadsadasdad");
                System.out.println(now);
                RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
                rpcResponseMessage.setReturnValue(promise.getNow());
//                RpcResponseMessage rpcResponseMessage = (RpcResponseMessage) promise.getNow();
                requestCallback.setRpcResponseMessage(rpcResponseMessage);
                return requestCallback;
            } else {
                System.out.println("服务调用失败");
                throw new LXYRouteServiceException("远程服务调用异常");
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new LXYRpcRuntimeException(e.getMessage());
        }
    }
}
