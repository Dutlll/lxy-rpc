package com.lxy.rpc.client.callback;

import com.lxy.rpc.callbacker.RPCRequestCallback;
import com.lxy.rpc.config.HotDeployment;
import com.lxy.rpc.config.LXYHotDeploymentConfig;
import com.lxy.rpc.constenum.ConstEnum;
import com.lxy.rpc.exception.LXYRpcConfigException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.lxy.rpc.callbacker.RPCRequestCallback;
import com.lxy.rpc.constenum.ConstEnum;
import com.lxy.rpc.config.HotDeployment;
import com.lxy.rpc.config.LXYHotDeploymentConfig;
import com.lxy.rpc.exception.LXYRpcConfigException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParalCallbackHandler implements InitializingBean , RPCCallbackInterface, HotDeployment {
    @Autowired
    private LXYHotDeploymentConfig lxyHotDeploymentConfig;

    private RPCCallbackInterface rpcCallbackInterface;

    public void init(){
        final String service_discover_RPCCallbackClass = lxyHotDeploymentConfig.<String>getProperty(ConstEnum.service_discover_RPCCallbackClass.getName());
        if ((service_discover_RPCCallbackClass.toLowerCase()).equals(SyncCallback.class.getSimpleName().toLowerCase())){
            rpcCallbackInterface = new ParalCallback();
        }
        else {
            throw new LXYRpcConfigException("找不到：service_discover_RPCCallbackClass的实现类");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @Override
    public RPCRequestCallback rpcCallback(RPCRequestCallback requestCallback) {
        return rpcCallbackInterface.rpcCallback(requestCallback);
    }

    @Override
    public void doHotDeploy() {
        init();
    }

    @Override
    public void doRegistInConfig() {
        lxyHotDeploymentConfig.registInHotDeplayQueue(this);
    }
}
