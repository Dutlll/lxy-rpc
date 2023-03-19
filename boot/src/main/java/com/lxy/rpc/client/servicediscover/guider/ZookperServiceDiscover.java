//package com.lxy.rpc.client.guider;
//
//import com.lxy.rpc.config.HotDeployment;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.CuratorFrameworkFactory;
//import org.apache.curator.retry.ExponentialBackoffRetry;
//import org.apache.zookeeper.data.Stat;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class ZookperServiceDiscover extends AbstractServiceDiscover implements HotDeployment {
//
//    // 集群连接地址
//    private final static String CONNECTSTRING = "***.***.***.***:2181,***.***.***.***:2181,***.***.***.***:2181,***.***.***.***:2181";
//
//    private CuratorFramework curatorFramework;
//
//    private void init(){
//        curatorFramework = CuratorFrameworkFactory.newClient(
//                CONNECTSTRING, 5000, 5000, new ExponentialBackoffRetry(1000, 3));
//        curatorFramework.start();
//    }
//
//    public ZookperServiceDiscover() {
//        init();
//    }
//
//
//
//    @Override
//    public List<String> getServices(String serviceGroupName) throws Exception {
//        // 查询/获取数据
//        Stat stat = new Stat();
//        byte[] bytes1 = curatorFramework.getData().storingStatIn(stat).forPath("/curator/curator1/curator11");
//        System.out.println(new String(bytes1) + " ---> stat: " + stat);
//        return null;
//    }
//
//    @Override
//    public IPGuider getIPGuider() {
//        return null;
//    }
//
//    @Override
//    public void doHotDeploy() {
//
//    }
//}
