package com.lxy.rpc.client.utils;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * global sheduledThreadPool which is for reflesh config
 * or other simple (will cost few time) task
 *
 * watch out never put the task which will cast long time
 */
@Component
public class LXYGlobalScheduledThreadPoolForSimpleTask {

    /**
     * 有一件事差点忘了：要记录和维护目标run列表：为什么，配置会更新，由于run实现类为应用类型
     * 在更新后重新创建线程池，然后把run列表重新放到线程池进行
     */
    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(
            1,
            new ThreadPoolTaskScheduler()
    );

    public static void putTaskAndRun(Runnable runnable){
        service.scheduleAtFixedRate(runnable,1, 1,TimeUnit.SECONDS);
    }

    public static void putTaskAndRun(Runnable runnable,int initialDaley,int period,TimeUnit timeUnit){
        service.scheduleAtFixedRate(
                runnable,
                initialDaley,
                period,
                timeUnit);
    }


    public static void main(String[] args) throws InterruptedException {
        LXYGlobalScheduledThreadPoolForSimpleTask.putTaskAndRun(()->{
            System.out.println(1);
            int oo = 1/0;
        });
        LXYGlobalScheduledThreadPoolForSimpleTask.putTaskAndRun(()->{
            System.out.println(2);

        });
        LXYGlobalScheduledThreadPoolForSimpleTask.putTaskAndRun(()->{
            System.out.println(3);
            int oo = 1/0;
        });
        TimeUnit.SECONDS.sleep(5);
        service.shutdown();
        System.out.println("-------");
//        service.
    }

}
