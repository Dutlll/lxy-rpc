package com.lxy.rpc.client.transmitter;

import com.lxy.rpc.constenum.ConstEnum;
import com.lxy.rpc.client.servicediscover.guider.ServiceDiscoverMode;
import com.lxy.rpc.client.utils.LXYGlobalScheduledThreadPoolForSimpleTask;
import com.lxy.rpc.config.HotDeployment;
import com.lxy.rpc.config.LXYAbstractConfig;
import com.lxy.rpc.config.LXYHotDeploymentConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * maintain every long connection to target remote service.
 */
@Component
public class LongConnectTransmitterPool implements Runnable, InitializingBean, HotDeployment {

    /**
     * because of there is the probably that heart beating check fail,
     * so we need to ensure the long conversation again,
     * this attribute is the necessary to do that.
     * and what in it are encapsulated , when config change will make a most few bad influence.
     * watch out : in order to make system normal work,
     * we should put retry long connection ' action is a true-while
     *
     * you could see detail in run method
     */
    @Autowired
    private ServiceDiscoverMode serviceDiscoverMode;



    /**
     * there is an channel maintained for every remote service
     * which is long connect ,will reduce the network io for request
     */
    private static ConcurrentHashMap<String,AbstractTransmitter> concurrentHashMap
            = new ConcurrentHashMap<String,AbstractTransmitter>();

    /**
     * @param serviceName
     * @return if tar service heart beating fail,will set available false ,and return null（constrain:watch out）
     */
    public AbstractTransmitter getTransmitter(String serviceName){
        final AbstractTransmitter abstractTransmitter = concurrentHashMap.get(serviceName);
        if (abstractTransmitter == null || abstractTransmitter.isAvailable.get() == false){
//          //不可达，返回null
            return null;
        }
        return abstractTransmitter;

    }

    public AbstractTransmitter removeTransmitter(String serviceName){
        return concurrentHashMap.remove(serviceName);
    }

    /**
     * in order to ensure the safety of concurrent runner ,
     * there is necessary to add synchronized
     * to lock instance
     * @param serviceName
     * @param abstractTransmitter
     */
    public synchronized void updateTransmitter(String serviceName,AbstractTransmitter abstractTransmitter){
        concurrentHashMap.remove(serviceName);
        concurrentHashMap.put(serviceName,abstractTransmitter);
    }

    /**
     * what is more , to ensure the channel get from the pool is accessible
     * we have heartbeat detect strategy,
     * this method is add heartbeat in a scheduled thread pool
     */
    @Override
    public void run() {
        /**
         * 去除每个长连接，定时做心跳检测，要同步建立一个队列，直接使用实例hash码建议，
         * 这里在队列放入每一个需要收到心跳检测回应的id，
         * （由于双方确定的方式较为麻烦，这里建议：先简单的ping包，然后后面的连接过程发送错误，反向修改这个类）
         * 一定要通过上面的API修改才是并发安全的
         */
        try {
            final Enumeration<String> keys = concurrentHashMap.keys();
            while (keys.hasMoreElements()){
                String tarKey = keys.nextElement();
                final AbstractTransmitter abstractTransmitter = concurrentHashMap.get(tarKey);
                try {
//                    Class<?> a=new Object().getClass();
//                    abstractTransmitter.doReqForRPC(new RpcRequestMessage(
//                            1,
//                            "","",a,new Class[0],new Object[0]));
                    abstractTransmitter.ping();
//                    .ping();
//                    rpcRequestPolicyBuilder.getRPCRequestPolicy().send(new PingMessage());
                }catch (Exception e){
                    //ping 失败，在重试一遍防止网络抖动，在失败，设置标记，在获取时候，会先重新建立连接然后使用请求
                    e.printStackTrace();
                    try {
                        abstractTransmitter.ping();
                    }catch (Exception ee){
                        ee.printStackTrace();
                        //将远程连接设置为不可用的，引发尝试重连
                        System.out.println("远程服务"+tarKey+" 不可达，重新设置标记位，抛出异常");
                        abstractTransmitter.isAvailable.compareAndSet(true,false);
                        //连接不可达关闭连接
                        abstractTransmitter.closeChannel();
                    }
                }
            }
        }catch (Exception e){
            System.out.println("全局定时任务池：有异常，但不能停止如下");
            e.printStackTrace();
        }
    }



    @Autowired
    private LXYAbstractConfig lxyAbstractConfig;

    @Override
    public void afterPropertiesSet() throws Exception {

        LXYGlobalScheduledThreadPoolForSimpleTask.putTaskAndRun(this,10,10, TimeUnit.SECONDS);
    }

    @Autowired
    private LXYHotDeploymentConfig lxyHotDeploymentConfig;

    private void configHeartBeatDetection(){
        Integer initialDaley = lxyHotDeploymentConfig.<Integer>getProperty(ConstEnum.service_discover_heartbeat_initialDaley.getName());
        Integer period = lxyAbstractConfig.<Integer>getProperty(ConstEnum.service_discover_heartbeat_period.getName());
        LXYGlobalScheduledThreadPoolForSimpleTask.putTaskAndRun(this,initialDaley,period, TimeUnit.SECONDS);
    }

    @Override
    public void doHotDeploy() {
        configHeartBeatDetection();
    }

    @Override
    public void doRegistInConfig() {
        lxyHotDeploymentConfig.registInHotDeplayQueue(this);
    }
}
