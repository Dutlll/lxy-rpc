package com.lxy.rpc.handler;



import com.lxy.rpc.bean.RpcRequestMessage;
import com.lxy.rpc.bean.RpcResponseMessage;
import com.lxy.rpc.service.HelloService;
import com.lxy.rpc.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        try {
            System.out.println(11119);
            RpcResponseMessage response = new RpcResponseMessage();
            response.setSequenceId(message.getSequenceId());
            try {
                final Class<?> aClass = Class.forName(message.getInterfaceName());
                final Object service = ServicesFactory.getService(aClass);
//                final Object service2 = ServicesFactory.getService(aClass);
//                HelloService service = (HelloService) service1;
//                Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
                Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
                Object invoke = method.invoke(service, message.getParameterValue());
                System.out.println("done");
                response.setReturnValue(invoke);
                TimeUnit.SECONDS.sleep(1);
                System.out.println("1111111111111114");
            } catch (Exception e) {
                System.out.println("1111111111111113");
                e.printStackTrace();
                try {
                    final Throwable cause = e.getCause();
                    String msg = cause.getMessage();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                log.debug("远程调用出错，BUG！！！！");
                response.setExceptionValue(new Exception("远程调用出错:"));
            }
            /**
             * 响应写回
             */
            System.out.println("1111111111111112");
            System.out.println(response.toString());
            ctx.writeAndFlush(response);
            System.out.println("1111111111111111");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "cn.itcast.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"}
        );
        HelloService service = (HelloService)
                ServicesFactory.getService(Class.forName(message.getInterfaceName()));
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }
}
