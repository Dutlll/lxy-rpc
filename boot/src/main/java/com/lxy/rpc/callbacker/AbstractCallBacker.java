package com.lxy.rpc.callbacker;

import io.netty.util.concurrent.Promise;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.concurrent.Callable;

/**
 * this Interface appoint :
 * the standard of 4 way to handler the rpc result
 * in order to distinguish the message , should use method with sequenceId
 */
public abstract class AbstractCallBacker {
    /**
     * this method is for Future RPC
     * @param tClass
     * @param sequenceId
     * @param <T>
     * @return
     */
    public abstract <T> T doCallBack(Class<T> tClass,String sequenceId);

    /**
     * one method may is not too powerful to deal 4 rpc way. so we need　polymorphic
     * this method is for Sync RPC
     * @param tClass
     * @param sequenceId
     * @param promise
     * @param <T>
     * @return
     */
    public abstract <T> T doCallBack(Class<T> tClass, String sequenceId, Promise promise);

    /**
     * this method is for callback RPC
     * @param tClass
     * @param sequenceId
     * @param callBack
     * @param <T>
     * @return
     */
    public abstract <T> T doCallBack(Class<T> tClass, String sequenceId, Runnable callBack);

    /**
     * this method is for oneway RPC
     */
    public void doCallBack(){
        //do nothing
    }

}
