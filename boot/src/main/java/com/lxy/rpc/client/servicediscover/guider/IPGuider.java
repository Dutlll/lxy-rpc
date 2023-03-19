package com.lxy.rpc.client.servicediscover.guider;

import com.lxy.rpc.exception.LXYRpcRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This Class is the guider for service provider
 * which has done effect for expand for other protocol,
 * now it contains IP and Port which is ths base information for request
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class IPGuider {
    private String ip;
    private String port;
    private String serviceName;

    public IPGuider(String ip,String port){
        this.ip = ip;
        this.port = port;
        try {
            Integer.valueOf(port);
        }catch (LXYRpcRuntimeException e){
            throw new LXYRpcRuntimeException("远程服务注册地址错误:ip"+ip+",port:"+port);
        }
    }
}
