package com.lxy.rpc.client.servicediscover.guider;

import com.lxy.rpc.constenum.ConstEnum;
import com.lxy.rpc.config.HotDeployment;
import com.lxy.rpc.config.LXYAbstractConfig;
import com.lxy.rpc.exception.LXYTimeOutException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceDiscoverMode implements InitializingBean, BeanFactoryAware, HotDeployment {

    private AbstractServiceDiscover abstractServiceDiscover;

    private BeanFactory beanFactory;

    @Autowired
    private LXYAbstractConfig lxyAbstractConfig;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public AbstractServiceDiscover getServiceDiscover(){
        if (this.abstractServiceDiscover == null) {
            //懒加载，spring加载过程可能没有拿到数据
            init();
            if (this.abstractServiceDiscover == null) {
                throw new LXYTimeOutException("无法连接到远程服务注册中心");
            }
        }
        return this.abstractServiceDiscover;
    }

    public void init(){
        String property = lxyAbstractConfig.<String>getProperty(ConstEnum.service_discover_class.getName());
        try {
            abstractServiceDiscover = (AbstractServiceDiscover) beanFactory.getBean(property);

        }catch (Exception e){
            //可能还没有加载完成，使用懒加载
        }
    }

    public AbstractServiceDiscover getAbstractServiceDiscoverByConfig(){
        String property = lxyAbstractConfig.<String>getProperty(ConstEnum.service_discover_class.getName());
        abstractServiceDiscover = (AbstractServiceDiscover) beanFactory.getBean(property);
        return abstractServiceDiscover;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
        doRegistInConfig();
    }

    @Override
    public void doHotDeploy() {
        init();
    }

    @Override
    public void doRegistInConfig() {
        lxyAbstractConfig.registInHotDeplayQueue(this);
    }
}
