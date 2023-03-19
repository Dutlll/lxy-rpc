package com.lxy.rpc.register;

import com.lxy.rpc.config.HotDeployment;
import com.lxy.rpc.constenum.ConstEnum;
import com.lxy.rpc.config.LXYHotDeploymentConfig;
import com.lxy.rpc.exception.LXYTimeOutException;
//import com.lxy.rpc.service.config.HotDeployment;
//import com.lxy.rpc.service.config.LXYHotDeploymentConfig;
//import com.lxy.rpc.service.exception.LXYTimeOutException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * this class is for service register
 * because of there is few way to do this so
 * (just register in zookeeper since program start run)
 */
@Component
public class StandardServiceRegister implements InitializingBean, HotDeployment {

    @Autowired
    private LXYHotDeploymentConfig lxyHotDeploymentConfig;


    CuratorFramework curatorFramework;

    private void init(){
        try {
            int sessionTimeoutMs = lxyHotDeploymentConfig
                    .<Integer>getProperty(ConstEnum.zookeeper_curator_sessionTimeoutMs.getName());

            int connectionTimeoutMs = lxyHotDeploymentConfig
                    .<Integer>getProperty(ConstEnum.zookeeper_curator_connectionTimeoutMs.getName());

            int baseSleepTimeMs = lxyHotDeploymentConfig
                    .<Integer>getProperty(ConstEnum.zookeeper_curator_baseSleepTimeMs.getName());

            int maxRetries = lxyHotDeploymentConfig
                    .<Integer>getProperty(ConstEnum.zookeeper_curator_maxRetries.getName());

            String connectString = lxyHotDeploymentConfig
                    .<String>getProperty(ConstEnum.zookeeper_curator_connectString.getName());

//        curatorFramework = CuratorFrameworkFactory.builder()
//                .connectString(connectString)
//                .sessionTimeoutMs(sessionTimeoutMs)
//                .connectionTimeoutMs(connectionTimeoutMs)
//                .r(maxRetries)
//                .build();

            String serviceName = lxyHotDeploymentConfig.<String>getProperty(ConstEnum.service_regist_serviceName.getName());

            String ip = lxyHotDeploymentConfig.<String>getProperty(ConstEnum.service_regist_ip.getName());

            Integer port = lxyHotDeploymentConfig.<Integer>getProperty(ConstEnum.service_regist_port.getName());

            curatorFramework = CuratorFrameworkFactory.newClient(
                    connectString,
                    sessionTimeoutMs,
                    connectionTimeoutMs,
                    new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries));
            curatorFramework.start();
            curatorFramework = curatorFramework.usingNamespace(serviceName);
            String node = "/"+ip+":"+port;
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(node);
        }catch (Exception e){
            e.printStackTrace();
//            if (curatorFramework == null){
//                throw new LXYTimeOutException("无法连接到注册中心");
//            }
            throw new LXYTimeOutException("无法连接到注册中心");

        }
    }

//    public static void main(String[] args) throws Exception {
//        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
//                "127.0.0.1:2181",
//                1000,
//                1000,
//                new RetryNTimes(3,1000));
//        curatorFramework.start();
////        curatorFramework.create().forPath("/test","我是内容".getBytes());
//        curatorFramework = curatorFramework.usingNamespace("test");
////        curatorFramework.getP
//        curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath("/123456");
//        System.in.read();
//    }
    @Override
    public void doHotDeploy() {
        init();
    }



    @Override
    public void doRegistInConfig() {
        lxyHotDeploymentConfig.registInHotDeplayQueue(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
