package com.lxy.rpc.log;


import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 rpc 的打印日志工厂
 *
 * @author <a href=mailto:guanchao.ygc@antfin.com>Guanchao Yang</a>
 */
public class RpcLoggerFactory {

    public static final String  RPC_LOG_SPACE = "com.alipay.sofa.rpc";

    private static final String APPNAME       = "appname";

    /**
     * 获取日志对象
     *
     * @param name 日志的名字
     * @return 日志实现
     */
    public static org.slf4j.Logger getLogger(String name, String appname) {
        return null;
//        //从"com/alipay/sofa/rpc/log"中获取 rpc 的日志配置并寻找对应logger对象,log 为默认添加的后缀
//        if (name == null || name.isEmpty()) {
//            return null;
//        }
//
//        Map<String, String> properties = new HashMap<String, String>();
//        properties.put(APPNAME, appname == null ? "" : appname);
//        SpaceId spaceId = new SpaceId(RPC_LOG_SPACE);
//        if (appname != null) {
//            spaceId.withTag(APPNAME, appname);
//        }
//        return LoggerSpaceManager.getLoggerBySpace(name, spaceId, properties);
    }
}
