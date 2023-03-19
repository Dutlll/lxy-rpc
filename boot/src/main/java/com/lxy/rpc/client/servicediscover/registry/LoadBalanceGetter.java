package com.lxy.rpc.client.servicediscover.registry;

import com.lxy.rpc.client.servicediscover.guider.IPGuider;
import com.lxy.rpc.exception.LXYRpcRuntimeException;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用方式：这个比较重要，
 * 这个注册表，在配置更新的时候可能是会有并发问题的，
 * 因此这个类应该在RemoteServiceAddrRegistry作为内部代码使用，
 * 在热配置更新的时候会使用类锁防止并发冲突，
 * 属性registry为逃逸属性，如果不按照上面使用是不安全的
 */
public class LoadBalanceGetter implements RequestPolicy{

    private LoadBalanceGetter() throws Exception {
        throw new Exception("需要传入注册表");
    }

    /**
     * loadBanlance impl
     * @param registry
     */
    public LoadBalanceGetter(ConcurrentHashMap<String, ArrayList<IPGuider>> registry){
        this.registry = registry;
    }

    private volatile ConcurrentHashMap<String, ArrayList<IPGuider>> registry;

    /**
     * the data source may change ,so we need setter to change it.
     * @param registry
     */
    public void setRegistry(ConcurrentHashMap<String, ArrayList<IPGuider>> registry) {
        this.registry = registry;
    }

    private static Random random = new Random();

    @Override
    public IPGuider getIPGuider(String serviceName) {
        ArrayList<IPGuider> tarList = registry.get(serviceName);
        if (tarList == null || tarList.size() == 0){
            throw new LXYRpcRuntimeException("注册表没有远程服务的地址信息");
        }

        final int nextInt = random.nextInt(tarList.size());
        return registry.get(serviceName).get(nextInt);
    }

}
