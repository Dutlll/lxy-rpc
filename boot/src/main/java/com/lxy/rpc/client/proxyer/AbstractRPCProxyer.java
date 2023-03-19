package com.lxy.rpc.client.proxyer;

/**
 * 构建目标rpc的代理类,动态代理实现远程调用入口
 */
public interface AbstractRPCProxyer {

    /**
     * 代理类生成方法
     * @param tarClass
     * @param <T>
     * @return
     */
    public <T> T getProxyObject(Class<T> tarClass);
}
