package com.lxy.rpc.client.proxyer;

//import cn.itcast.message.RpcRequestMessage;
//import cn.itcast.protocol.SequenceIdGenerator;
import com.lxy.rpc.bean.RpcRequestMessage;
import com.lxy.rpc.callbacker.RPCRequestCallback;
import com.lxy.rpc.callbacker.AbstractCallBacker;
import com.lxy.rpc.annotation.LXYServiceComsumer;
import com.lxy.rpc.client.callback.ParalCallback;
import com.lxy.rpc.client.callback.StandardCallbackHandler;
import com.lxy.rpc.client.servicediscover.guider.ServiceDiscoverMode;
import com.lxy.rpc.client.transmitter.AbstractTransmitter;
import com.lxy.rpc.client.transmitter.LongConnectTransmitterPool;
import com.lxy.rpc.client.transmitter.policy.RPCRequestPolicy;
import com.lxy.rpc.client.transmitter.TransmitterFactory;
import com.lxy.rpc.client.transmitter.policy.RPCRequestPolicyBuilder;
import com.lxy.rpc.config.LXYHotDeploymentConfig;
import com.lxy.rpc.protocol.SequenceIdGenerator;
import io.netty.util.concurrent.Promise;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * this class is the standard impl for RPC proxy
 */
@Component
public class StandardAbstractRPCProxyer implements AbstractRPCProxyer, InitializingBean {

    @Autowired
    private LXYHotDeploymentConfig lxyHotDeploymentConfig;

    //一下组合的类具有一下特性：１，根据配置生成，２，实现了配置热更新策略，在配置中心收到更新后调用更新信息
    //根据怕配置策略加载动态代理实现模式
    private ProxyMode proxyMode;

    //服务器发现方式
    private ServiceDiscoverMode serviceDiscoverMode;

    //网络传输方式
    private AbstractTransmitter abstractTransmitter;

    //处理返回结果的方式
    private AbstractCallBacker abstractCallBacker;

    @Autowired
    private LongConnectTransmitterPool longConnectTransmitterPool;

    @Autowired
    private TransmitterFactory transmitterFactory;

    @Autowired
    private RPCRequestPolicyBuilder rpcRequestPolicyBuilder;

    @Autowired
    private StandardCallbackHandler standardCallbackHandler;

    /**
     * 代理类生成方法
     * @param tarClass
     * @param <T>
     * @return
     */
    public <T> T
    getProxyObject(Class<T> tarClass) {
        ClassLoader loader = tarClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{tarClass};

        //注意：注解可以实现更强的功能，在这里可以使用池化技术，对不同的注解使用不同的处理bean，但需要手动配置注入bean，且不可以使用热配置

        //1,通过代理策略，完成接口反射出目标代理类的具体实现
        //                                                            sayHello  "张三"
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            // 1. 将方法调用转换为 消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    tarClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );

            //获取注解：服务集群的名称
            LXYServiceComsumer serviceComsumer = tarClass.getAnnotation(LXYServiceComsumer.class);
            String tarServiceName = serviceComsumer.tarServiceName();

            msg.setServiceName(tarServiceName);

            /**
             * 哈哈，这个时候是版本1，纪念一下：
             */
            final RPCRequestPolicy rpcRequestPolicy = rpcRequestPolicyBuilder.getRPCRequestPolicy();
            /**
             * 下面我们将使用 spi，使用自定义的spi机制管理配置（原来是在）rpcRequestPolicyBuilder内部需要维护配置的
             * 这个升级牵动较大，干脆升级版本吧2.0使用spi（自定义模块和组件管理）和spring（jar包管理）双工厂
             * 计划，
             * 画构造图 --》力求组件化 -》 写代码
             */

            final RPCRequestCallback send = rpcRequestPolicy.send(msg);

//            //根据连接策略，决定怎么拿到管道（默认实现，长连接+负载均衡），真正实现（最大限度）远程服务--》本地服务
//            //2,根据配置选择服务发现的实现方式
//            IPGuider ipGuider = serviceDiscoverMode.getServiceDiscover().getIPGuider(tarServiceName);
//
//            //3,根据连接策略拿到channel,for发送数据
//            final AbstractTransmitter transmitter = longConnectTransmitterPool.getTransmitter(msg);

            //4,根据处理策略，处理服务端的响应
            try {
                final RPCRequestCallback rpcRequestCallback = standardCallbackHandler.rpcCallback(send);
//                rpcRequestCallback.getId();//
                return rpcRequestCallback.getRpcResponseMessage().getReturnValue();
//                return standardCallbackHandler.rpcCallback(send);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception();
//                return standardCallbackHandler.rpcCallback(send);
            }
        });
        //返回方法'执行'结果,以上均为代理操作
        return (T) o;
    }

    /**
     * this is class is dependence LXYHotDeployment so, should init after all dependence are ready
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        serviceDiscoverMode = new ServiceDiscoverMode();

        abstractTransmitter = null;
//                = new AbstractTransmitter() {
//            @Override
//            public Channel doReqForRPC(RpcRequestMessage rpcRequestMessage) {
//                return null;
//            }
//
//            @Override
//            public void ping() throws LXYTimeOutException {
//
//            }
//        };

        abstractCallBacker = new AbstractCallBacker() {
            @Override
            public <T> T doCallBack(Class<T> tClass, String sequenceId) {
                return null;
            }

            @Override
            public <T> T doCallBack(Class<T> tClass, String sequenceId, Promise promise) {
                return null;
            }

            @Override
            public <T> T doCallBack(Class<T> tClass, String sequenceId, Runnable callBack) {
                return null;
            }
        };
    }
}
