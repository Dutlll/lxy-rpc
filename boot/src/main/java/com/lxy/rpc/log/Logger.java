package com.lxy.rpc.log;

public interface Logger {

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Is debug enabled boolean.
     *
     * @return the boolean
     */
    boolean isDebugEnabled();

    /**
     * Debug.
     *
     * @param message the message
     */
    void debug(String message);

    /**
     * Debug.
     *
     * @param format the format
     * @param args   the args
     */
    void debug(String format, Object... args);

    /**
     * Debug.
     *
     * @param message the message
     * @param t       the t
     */
    void debug(String message, Throwable t);

    /**
     * Is debug enabled boolean.
     *
     * @param appName the app name
     * @return the boolean
     */
    boolean isDebugEnabled(String appName);

    /**
     * Debug with app.
     *
     * @param appName the app name
     * @param message the message
     */
    void debugWithApp(String appName, String message);

    /**
     * Debug with app.
     *
     * @param appName the app name
     * @param format  the format
     * @param args    the args
     */
    void debugWithApp(String appName, String format, Object... args);

    /**
     * Debug with app.
     *
     * @param appName the app name
     * @param message the message
     * @param t       the t
     */
    void debugWithApp(String appName, String message, Throwable t);

    /**
     * Is info enabled boolean.
     *
     * @return the boolean
     */
    boolean isInfoEnabled();

    /**
     * Info.
     *
     * @param message the message
     */
    void info(String message);

    /**
     * Info.
     *
     * @param format the format
     * @param args   the args
     */
    void info(String format, Object... args);

    /**
     * Info.
     *
     * @param message the message
     * @param t       the t
     */
    void info(String message, Throwable t);

    /**
     * Is info enabled boolean.
     *
     * @param appName the app name
     * @return the boolean
     */
    boolean isInfoEnabled(String appName);

    /**
     * Info with app.
     *
     * @param appName the app name
     * @param message the message
     */
    void infoWithApp(String appName, String message);

    /**
     * Info with app.
     *
     * @param appName the app name
     * @param format  the format
     * @param args    the args
     */
    void infoWithApp(String appName, String format, Object... args);

    /**
     * Info with app.
     *
     * @param appName the app name
     * @param message the message
     * @param t       the t
     */
    void infoWithApp(String appName, String message, Throwable t);

    /**
     * Is warn enabled boolean.
     *
     * @return the boolean
     */
    boolean isWarnEnabled();

    /**
     * Warn.
     *
     * @param message the message
     */
    void warn(String message);

    /**
     * Warn.
     *
     * @param format the format
     * @param args   the args
     */
    void warn(String format, Object... args);

    /**
     * Warn.
     *
     * @param message the message
     * @param t       the t
     */
    void warn(String message, Throwable t);

    /**
     * Is warn enabled boolean.
     *
     * @param appName the app name
     * @return the boolean
     */
    boolean isWarnEnabled(String appName);

    /**
     * Warn with app.
     *
     * @param appName the app name
     * @param message the message
     */
    void warnWithApp(String appName, String message);

    /**
     * Warn with app.
     *
     * @param appName the app name
     * @param format  the format
     * @param args    the args
     */
    void warnWithApp(String appName, String format, Object... args);

    /**
     * Warn with app.
     *
     * @param appName the app name
     * @param message the message
     * @param t       the t
     */
    void warnWithApp(String appName, String message, Throwable t);

    /**
     * Is error enabled boolean.
     *
     * @return the boolean
     */
    boolean isErrorEnabled();

    /**
     * Error.
     *
     * @param message the message
     */
    void error(String message);

    /**
     * Error.
     *
     * @param format the format
     * @param args   the args
     */
    void error(String format, Object... args);

    /**
     * Error.
     *
     * @param message the message
     * @param t       the t
     */
    void error(String message, Throwable t);

    /**
     * Is error enabled boolean.
     *
     * @param appName the app name
     * @return the boolean
     */
    boolean isErrorEnabled(String appName);

    /**
     * Error with app.
     *
     * @param appName the app name
     * @param message the message
     */
    void errorWithApp(String appName, String message);

    /**
     * Error with app.
     *
     * @param appName the app name
     * @param format  the format
     * @param args    the args
     */
    void errorWithApp(String appName, String format, Object... args);

    /**
     * Error with app.
     *
     * @param appName the app name
     * @param message the message
     * @param t       the t
     */
    void errorWithApp(String appName, String message, Throwable t);

}
