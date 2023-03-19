package com.lxy.rpc;

import com.lxy.rpc.spi.ExtensionLoader;
import com.lxy.rpc.spi.LogLXY;

public class AD {
    public static void main(String[] args) {
        try {
            Class.forName("com.lxy.rpc.expand.Helloword1");
        }catch (Exception e){
            e.printStackTrace();
        }
        final ExtensionLoader<LogLXY> logLXYExtensionLoader = new ExtensionLoader<LogLXY>(LogLXY.class);
        final LogLXY aaa = logLXYExtensionLoader.getExtension("adasd");
        final LogLXY defaultExtension = logLXYExtensionLoader.getDefaultExtension();
        defaultExtension.hello();
        System.out.println("111");
        aaa.hello();
    }
}
