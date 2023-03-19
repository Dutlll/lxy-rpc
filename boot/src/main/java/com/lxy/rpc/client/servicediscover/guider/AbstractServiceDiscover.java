package com.lxy.rpc.client.servicediscover.guider;

import com.lxy.rpc.exception.LXYRpcRuntimeException;

import java.util.List;

/**
 * 服务发现者
 * 负责通信
 */
public abstract class AbstractServiceDiscover {
    /**
     * 在实现之前有必要概述一下类似获取方法：
     * 在nacos的使用里，我们一般会给服务提供者一个名字
     * 因此在这里我们也有必要如此处理，
     *
     * 1，服务提供者，要配置名字
     * 2，服务消费者，@FeignClient("job-user")如此，以便可以正确定位到目标服务
     *
     * FeignClient的实现是站在HTTP层面请求，这里我采用自定义协议
     * 序列化的实现
     * 两者的全路径类名（会提供统一接口包）一一对应即可（约束）
     *
     * 实现：对于自定义协议的协议实现：
     * 规范：两端使用相同的全路径类名，调用者在注解上可以获取服务集群的名称
     * 这里可以干什么：可以获取到注册表，进行下一步调用
     *
     *
     */

    /**
     * return the remote IP of service provider
     * @param serviceGroupName the nick name which is zhe service ID
     * @return the remote IP list which is zhe chape of String,from zhe service register such as Zookeeper
     * @throws if we can not connect to the tar service discover we will throw connect fail exception
     */
    public abstract List<String> getServices(String serviceGroupName) throws Exception;

    /**
     * get tar service ip by the strategies　that we have configed.
     * @return
     */
    public abstract IPGuider getIPGuider(String serviceGroupName) throws LXYRpcRuntimeException;

}
