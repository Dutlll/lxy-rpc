package com.lxy.rpc.client.transmitter;

import com.lxy.rpc.constenum.ConstEnum;
import com.lxy.rpc.client.servicediscover.guider.ServiceDiscoverMode;
import com.lxy.rpc.client.transmitter.policy.LongConnectionRPCRequestPolicy;
import com.lxy.rpc.client.transmitter.policy.RPCRequestPolicy;
import com.lxy.rpc.client.transmitter.policy.RPCRequestPolicyBuilder;
import com.lxy.rpc.config.HotDeployment;
import com.lxy.rpc.config.LXYHotDeploymentConfig;
import com.lxy.rpc.exception.LXYRpcRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StandartRPCRequestPolicyBuilder extends RPCRequestPolicyBuilder implements HotDeployment {

    @Autowired
    private LXYHotDeploymentConfig lxyHotDeploymentConfig;

    @Autowired
    private LongConnectTransmitterPool longConnectTransmitterPool;

    @Autowired
    private ServiceDiscoverMode serviceDiscoverMode;

    @Autowired
    private TransmitterFactory transmitterFactory;

    private RPCRequestPolicy rpcRequestPolicy;

    public void init(){
        String policyName = lxyHotDeploymentConfig.<String>getProperty(ConstEnum.service_request_RPCRequestPolicy.getName());
        if (policyName.toLowerCase().equals(LongConnectionRPCRequestPolicy.class.getSimpleName().toLowerCase())){
            try{
                LongConnectionRPCRequestPolicy longConnectionRPCRequestPolicy = new LongConnectionRPCRequestPolicy();
                longConnectionRPCRequestPolicy.setLongConnectTransmitterPool(longConnectTransmitterPool);
                longConnectionRPCRequestPolicy.setServiceDiscoverMode(serviceDiscoverMode);
                longConnectionRPCRequestPolicy.setTransmitterFactory(transmitterFactory);
                rpcRequestPolicy = longConnectionRPCRequestPolicy;
            }catch (Exception e){
                throw new LXYRpcRuntimeException("配置请求策略错误:LongConnectionRPCRequestPolicy");
            }
        }
    }

    @Override
    public RPCRequestPolicy getRPCRequestPolicy() {
        if (rpcRequestPolicy == null){
            init();
        }
        return rpcRequestPolicy;
    }

    @Override
    public void doHotDeploy() {
        init();
    }

    @Override
    public void doRegistInConfig() {
        this.lxyHotDeploymentConfig.registInHotDeplayQueue(this);
    }
}
