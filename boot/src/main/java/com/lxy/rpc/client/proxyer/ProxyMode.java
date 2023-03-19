package com.lxy.rpc.client.proxyer;

import sun.reflect.CallerSensitive;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public abstract class ProxyMode {

    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h) throws IllegalArgumentException
    {
        return null;
    }
}
