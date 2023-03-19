package com.lxy.rpc.client.servicediscover.registry;

import com.lxy.rpc.client.servicediscover.guider.IPGuider;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public interface RequestPolicy {
    public IPGuider getIPGuider(String serviceName);

    public void setRegistry(ConcurrentHashMap<String, ArrayList<IPGuider>> registry);
}
