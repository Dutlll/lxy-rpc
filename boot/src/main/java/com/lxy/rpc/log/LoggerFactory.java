package com.lxy.rpc.log;

/**
 * Factory of logger.
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class LoggerFactory {

    /**
     * 配置的实现类
     */
    private static String implClass = "";

    public static Logger getLogger(String name) throws Exception {
        try {
            Object logInstance = new MiddlewareLoggerImpl(implClass);
            if (logInstance instanceof Logger) {
                return (Logger) logInstance;
            } else {
                throw new Exception(implClass + " is not type of  " + Logger.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new Exception(implClass + " is not type of  " + Logger.class);

    }

    public static Logger getLogger(Class clazz) throws Exception {
        try {
            Object logInstance = new MiddlewareLoggerImpl(clazz);
            if (logInstance instanceof Logger) {
                return (Logger) logInstance;
            } else {
                throw new Exception(implClass + " is not type of  " + Logger.class);
            }
        } catch (Exception e) {
            throw new Exception("Error when getLogger of " + clazz.getName()
                    + ", implement is " + implClass + "", e);
        }
    }
}
