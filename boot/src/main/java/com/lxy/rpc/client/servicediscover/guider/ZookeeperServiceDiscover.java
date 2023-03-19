package com.lxy.rpc.client.servicediscover.guider;

import com.lxy.rpc.constenum.ConstEnum;
import com.lxy.rpc.client.servicediscover.registry.RemoteServiceAddrRegistry;
import com.lxy.rpc.client.utils.LXYGlobalScheduledThreadPoolForSimpleTask;
import com.lxy.rpc.config.HotDeployment;
import com.lxy.rpc.config.LXYAbstractConfig;
import com.lxy.rpc.exception.LXYRpcRuntimeException;
import com.lxy.rpc.exception.LXYTimeOutException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this class is impl for ServiceDiscover for Zookeeper
 */
@Component
public class ZookeeperServiceDiscover
        extends AbstractServiceDiscover
        implements HotDeployment, InitializingBean,Runnable
{

    @Autowired
    private LXYAbstractConfig lxyHotDeploymentConfig;

    @Autowired
    private LXYGlobalScheduledThreadPoolForSimpleTask lxyGlobalScheduledThreadPoolForSimpleTask;

    private CuratorFramework curatorFramework;

    //是否使用注册表
    private volatile boolean useRegistryCache;

    //远程服务地址注册表
    private RemoteServiceAddrRegistry remoteServiceAddrRegistry = new RemoteServiceAddrRegistry();;

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

            useRegistryCache = lxyHotDeploymentConfig
                    .<String>getProperty(ConstEnum.zookeeper_getstrategy.getName()).equals("default");


            String connectString = lxyHotDeploymentConfig
                    .<String>getProperty(ConstEnum.zookeeper_curator_connectString.getName());

//        curatorFramework = CuratorFrameworkFactory.builder()
//                .connectString(connectString)
//                .sessionTimeoutMs(sessionTimeoutMs)
//                .connectionTimeoutMs(connectionTimeoutMs)
//                .r(maxRetries)
//                .build();

            curatorFramework = CuratorFrameworkFactory.newClient(
                    connectString,
                    sessionTimeoutMs,
                    connectionTimeoutMs,
                    new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries));
            curatorFramework.start();

        }catch (Exception e){
            e.printStackTrace();
            if (curatorFramework == null){
                throw new LXYTimeOutException("无法连接到注册中心");
            }
        }
    }

    /**
     * we will see tar node as our service group  name ,and then get service list from tar node.
     * what is more, will cache or make actions more effective by config strategy
     * @param serviceGroupName the nick name which is zhe service ID
     * @return the service list which is the shape of ip:port (type String)
     * @throws Exception
     */
    @Override
    public List<String> getServices(String serviceGroupName) throws LXYTimeOutException {
        // 查询/获取数据
        List<String> tarServiceList = getServicesOnly(serviceGroupName);

        // 是否使用注册表
        if (useRegistryCache == true){
            //缓存
            if (remoteServiceAddrRegistry == null)
                remoteServiceAddrRegistry = new RemoteServiceAddrRegistry();
            remoteServiceAddrRegistry.putRemoteServiceGroup(serviceGroupName,tarServiceList);
            //还要根据配置设置过期时间的定时任务
        }

        return tarServiceList;
    }

    /**
     * this is the method which truely get data from service,
     * for getServices
     * @param serviceGroupName
     * @return the service list which is the shape of ip:port (type String)
     * @throws Exception
     */
    private List<String> getServicesOnly(String serviceGroupName) throws LXYTimeOutException {
        try {
            // 查询/获取数据
            Stat stat = new Stat();
            List<String> tarServiceList = curatorFramework.getChildren().storingStatIn(stat).forPath(serviceGroupName);
            return tarServiceList;
        }catch (Exception e){
            //运行重试一次

            throw new LXYTimeOutException("无法获取注册中心信息");
        }
    }

    private List<String> getServicesOnly(String serviceGroupName,int retryTime) throws LXYTimeOutException {
        try {
            return getServicesOnly(serviceGroupName);
        }catch (LXYTimeOutException e){
            retryTime--;
            return getServicesOnly(serviceGroupName,retryTime);
        }
    }


    private volatile AtomicBoolean isServiceLock = new AtomicBoolean(false);
    /**
     * 由于内部可能有缓存等，注册表信息，如果直接去会有并发问题，因此这里要上锁
     * 建议使用cas
     * @param serviceGroupName
     * @return
     */
    @Override
    public IPGuider getIPGuider(String serviceGroupName) throws LXYTimeOutException {

        //使用注册表
        if (useRegistryCache == true) {
            if (isServiceLock.get() == true) {
                //上锁了,以后也可以配置，使用阻塞队列，配置完后更新，这里会报错
                throw new LXYRpcRuntimeException("正在更新配置，暂时中断服务");
            }else {
                try {
                    return remoteServiceAddrRegistry.getIPGuider(serviceGroupName);
                }catch (LXYRpcRuntimeException e){
                    getServices(serviceGroupName);
                    return remoteServiceAddrRegistry.getIPGuider(serviceGroupName);
                }catch (LXYTimeOutException e){
                    //如果没有回抛出异常，这里重新更新配置远程路由表
                    List<String> serviceRemoteIpList = getServicesOnly(serviceGroupName);
                    remoteServiceAddrRegistry.putRemoteServiceGroup(serviceGroupName,serviceRemoteIpList);
                }
            }
        }
        throw new LXYTimeOutException("无法连接到远程主机");
//        //直接获取，然后采用负载均衡
//        List<String> servicesOnly = null;
//        try {
//            servicesOnly = getServicesOnly(serviceGroupName);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        if (servicesOnly == null)
//            throw new LXYRpcRuntimeException("注册中心没有目标主机集群的访问地址:"+serviceGroupName);
//        final String[] split = servicesOnly.get(random.nextInt(servicesOnly.size())).split(":");
//        return new IPGuider(split[0],split[1]);
    }

    private Random random = new Random();

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
        //根据配置进行初始话
        init();
        //使用了config的应该注册到热配置组件
        lxyHotDeploymentConfig.registInHotDeplayQueue(this);
        //将自己的定时任务注册到全局进行
        LXYGlobalScheduledThreadPoolForSimpleTask.putTaskAndRun(this);
    }

    @Override
    public void run() {
        try {

            //执行定时任务更新之，更新远程服务名单
            final List<String> serviceNameList = remoteServiceAddrRegistry.getServiceNameList();

            serviceNameList.add("/test");
            //1，先获取目标
            final HashMap<String, List<String>> cache = new HashMap<>();
            for (String serviceName : serviceNameList) {
                List<String> servicesOnly = null;
                try {
                    servicesOnly = getServicesOnly(serviceName);
                    if (servicesOnly == null) {
                        throw new Exception("null servicesOnly return");
                    }
                } catch (Exception e) {
                    System.out.println("定时更新策略发送异常，为了正常工作，这里不进行更新");
                    e.printStackTrace();
                }
                cache.put(serviceName, servicesOnly);
            }
            //2,上锁，一次cas即可
            final boolean b = isServiceLock.compareAndSet(false, true);
            if (isServiceLock.get() == false) {
                isServiceLock.compareAndSet(false, true);
                if (isServiceLock.get() == false) {
                    System.out.println("定时更新策略发送异常：上锁失败");
                }
            }
            //上锁完毕，开始更新注册表
            remoteServiceAddrRegistry = new RemoteServiceAddrRegistry();
            final Set<String> keySet = cache.keySet();
            for (String s : keySet) {
                remoteServiceAddrRegistry.putRemoteServiceGroup(s, cache.get(s));
            }
        }catch (Exception e){
            System.out.println("出现异常了，但这个定时任务不可以停止");
            e.printStackTrace();
        }finally {
            //开锁
            if (isServiceLock.get() == true) {
                isServiceLock.compareAndSet(true, false);
                if (isServiceLock.get() == true) {
                    isServiceLock.compareAndSet(true, false);
                    if (isServiceLock.get() == true) {
                        System.out.println("BUG!!!,致命异常，无法解锁，开始自旋解锁");
                        while (isServiceLock.compareAndSet(true, false) != true) {

                        }
                    }
                }
            }
        }
    }
}
