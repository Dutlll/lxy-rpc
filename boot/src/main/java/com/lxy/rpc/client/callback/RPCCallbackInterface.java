package com.lxy.rpc.client.callback;

import com.lxy.rpc.callbacker.RPCRequestCallback;

public interface RPCCallbackInterface {
    public RPCRequestCallback rpcCallback(RPCRequestCallback requestCallback);
}
