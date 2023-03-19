package com.lxy.rpc.client.servicediscover.registry;

import com.lxy.rpc.constenum.ConstEnum;
import com.lxy.rpc.client.servicediscover.guider.IPGuider;
import com.lxy.rpc.config.HotDeployment;
import com.lxy.rpc.config.LXYAbstractConfig;
import com.lxy.rpc.config.LXYHotDeploymentConfig;
import com.lxy.rpc.exception.LXYRpcRuntimeException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * will record the remote service address ,
 * and according to the config to make a choice to request way
 */
@Component
public class RemoteServiceAddrRegistry implements HotDeployment , InitializingBean {

    private LXYAbstractConfig lxyHotDeploymentConfig = new LXYHotDeploymentConfig();

    private RequestPolicy requestPolicy;

    /**
     * registry
     */
    private ConcurrentHashMap<String, ArrayList<IPGuider>> registry =
            new ConcurrentHashMap<String, ArrayList<IPGuider>>();

    public void putRemoteServiceGroup(String serviceName, List<String> serviceGroup){
        final ArrayList<IPGuider> ipGuiders = new ArrayList<>();
        for (String s : serviceGroup){
            final String[] split = s.split(":");
            ipGuiders.add(new IPGuider(split[0],split[1]));
        }
        registry.put(serviceName,ipGuiders);
    }

    public List<String> getServiceNameList(){
        Enumeration<String> keys = registry.keys();
        ArrayList<String> strings = new ArrayList<>();
        while (keys.hasMoreElements()){
            strings.add(keys.nextElement());
        }
        return strings;
    }

    /**
     *
     * @return
     */
    public IPGuider getIPGuider(String serviceName){
        if (requestPolicy == null)
            loadRequestPolicy();
        try {
            return requestPolicy.getIPGuider(serviceName);
        }catch (LXYRpcRuntimeException lxyRpcRuntimeException){
            throw new LXYRpcRuntimeException("没有远程服务的地址信息，尝试请求");
        }
    }

    @Override
    public void doHotDeploy() {
        lxyHotDeploymentConfig.registInHotDeplayQueue(this);
        if (requestPolicy == null){
            requestPolicy = new LoadBalanceGetter(registry);
        } else {
            requestPolicy = new LoadBalanceGetter(registry);
        }
    }

    @Override
    public void doRegistInConfig() {

    }

    private void loadRequestPolicy(){
        String remoteip_choice_strategy = lxyHotDeploymentConfig.<String>getProperty(
                ConstEnum.remoteip_choice_strategy.getName()
        );
        //默认为负载均衡算法
        if (remoteip_choice_strategy.equals("default")){
            requestPolicy = new LoadBalanceGetter(registry);
        }else {
            requestPolicy = new LoadBalanceGetter(registry);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadRequestPolicy();
        //否则使用反射获取
    }
}
