package com.lxy.rpc.client.handler;

import com.lxy.rpc.bean.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import sun.nio.ch.ThreadPool;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    //                       序号      用来接收结果的 promise 对象
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();
    public static final Map<Integer, Runnable> PARAL_PROMISES = new ConcurrentHashMap<>();
    public static final Map<Integer, RpcResponseMessage> RES_MAP = new ConcurrentHashMap<>();


    public static void submit(List<Integer> taskId, Runnable r){
        // id -> List<>  id -> list pop id           id -> list empty -> exeRun
    }
    public static void submit(Integer id, Runnable r){
        PARAL_PROMISES.put(id,r);
    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        try {
            log.debug("Message===============");
            log.debug("{}", msg);
            // 拿到空的 promise
//            Promise<Object> promise = PROMISES.remove(msg.getSequenceId());
            //任务驱动
            RES_MAP.put(msg.getSequenceId(),msg);
            executorService.submit(PARAL_PROMISES.get(msg.getSequenceId()));
//            PARAL_PROMISES.put(msg.getSequenceId(), promise);
//            if (promise != null) {
//                Object returnValue = msg.getReturnValue();
//                Exception exceptionValue = msg.getExceptionValue();
//                if (exceptionValue != null) {
//                    promise.setFailure(exceptionValue);
//                } else {
//                    promise.setSuccess(returnValue);
//                }
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
