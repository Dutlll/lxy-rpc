package com.lxy.rpc.spi;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

//供spring bean使用
@Component
public class ExtensionLoaderFactory {
    private static ExtensionLoader extensionLoader;
    static {
        extensionLoader = new ExtensionLoader();
    }

    private ConcurrentHashMap<Class<?>,ExtensionLoader<?>> factory = new ConcurrentHashMap<>();

    /**
     * 核心： ExtensionLoader内部的 EXTENSION_LOADERS（工厂本质） = new ConcurrentHashMap();
     * 这个类就是适配器类，作为沟通spring 和本地桥梁
     */
    public ExtensionLoader<?> getExtensionLoaderByClass(Class<?> clazz){
        /**
         * 内部在使用一层缓存
         */
        ExtensionLoader<?> tar = factory.get(clazz);
        if (tar == null) {
            ExtensionLoader<?> extensionLoader = ExtensionLoader.getExtensionLoader(clazz);
            factory.put(clazz, extensionLoader);
            return extensionLoader;
        } else {
            return tar;
        }
    }
}
