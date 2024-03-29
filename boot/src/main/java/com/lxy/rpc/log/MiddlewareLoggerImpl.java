package com.lxy.rpc.log;

/**
 * 中间件日志输出类，对外的时候由于不存在合并部署的情况，目前不支持按应用分目录。
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class MiddlewareLoggerImpl implements Logger {

    private final String           name;

    private final org.slf4j.Logger DEFAULT_LOGGER;

    public MiddlewareLoggerImpl(String name) {
        this.name = name;
        DEFAULT_LOGGER = RpcLoggerFactory.getLogger(name, null);
    }

    public MiddlewareLoggerImpl(Class clazz) {
        this.name = clazz.getCanonicalName();
        DEFAULT_LOGGER = RpcLoggerFactory.getLogger(name, null);
    }

    private org.slf4j.Logger getLogger() {
        return DEFAULT_LOGGER;
    }

    private org.slf4j.Logger getLogger(String appName) {
        return DEFAULT_LOGGER;
    }

    @Override
    public String getName() {
        return getLogger().getName();
    }

    @Override
    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    @Override
    public void debug(String message) {
        getLogger().debug(message);
    }

    @Override
    public void debug(String format, Object... args) {
        getLogger().debug(format, args);
    }

    @Override
    public void debug(String message, Throwable t) {
        getLogger().debug(message, t);
    }

    @Override
    public boolean isDebugEnabled(String appName) {
        return getLogger(appName).isDebugEnabled();
    }

    @Override
    public void debugWithApp(String appName, String message) {
        getLogger(appName).debug(message);
    }

    @Override
    public void debugWithApp(String appName, String format, Object... args) {
        getLogger(appName).debug(format, args);
    }

    @Override
    public void debugWithApp(String appName, String message, Throwable t) {
        getLogger(appName).debug(message, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    @Override
    public void info(String message) {
        getLogger().info(message);
    }

    @Override
    public void info(String format, Object... args) {
        getLogger().info(format, args);
    }

    @Override
    public void info(String message, Throwable t) {
        getLogger().info(message, t);
    }

    @Override
    public boolean isInfoEnabled(String appName) {
        return getLogger(appName).isInfoEnabled();
    }

    @Override
    public void infoWithApp(String appName, String message) {
        getLogger(appName).info(message);
    }

    @Override
    public void infoWithApp(String appName, String format, Object... args) {
        getLogger(appName).info(format, args);
    }

    @Override
    public void infoWithApp(String appName, String message, Throwable t) {
        getLogger(appName).info(message, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return getLogger().isWarnEnabled();
    }

    @Override
    public void warn(String message) {
        getLogger().warn(message);
    }

    @Override
    public void warn(String format, Object... args) {
        getLogger().warn(format, args);
    }

    @Override
    public void warn(String message, Throwable t) {
        getLogger().warn(message, t);
    }

    @Override
    public boolean isWarnEnabled(String appName) {
        return getLogger(appName).isWarnEnabled();
    }

    @Override
    public void warnWithApp(String appName, String message) {
        getLogger(appName).warn(message);
    }

    @Override
    public void warnWithApp(String appName, String format, Object... args) {
        getLogger(appName).warn(format, args);
    }

    @Override
    public void warnWithApp(String appName, String message, Throwable t) {
        getLogger(appName).warn(message, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return getLogger().isErrorEnabled();
    }

    @Override
    public void error(String message) {
        getLogger().error(message);
    }

    @Override
    public void error(String format, Object... args) {
        getLogger().error(format, args);
    }

    @Override
    public void error(String message, Throwable t) {
        getLogger().error(message, t);
    }

    @Override
    public boolean isErrorEnabled(String appName) {
        return getLogger(appName).isErrorEnabled();
    }

    @Override
    public void errorWithApp(String appName, String message) {
        getLogger(appName).error(message);
    }

    @Override
    public void errorWithApp(String appName, String format, Object... args) {
        getLogger(appName).error(format, args);
    }

    @Override
    public void errorWithApp(String appName, String message, Throwable t) {
        getLogger(appName).error(message, t);
    }

}
