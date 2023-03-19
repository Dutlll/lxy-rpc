package com.lxy.rpc.server;

import com.lxy.rpc.constenum.ConstEnum;
import com.lxy.rpc.config.HotDeployment;
import com.lxy.rpc.config.LXYHotDeploymentConfig;
import com.lxy.rpc.protocol.MessageCodecSharable;
import com.lxy.rpc.protocol.ProcotolFrameDecoder;
import com.lxy.rpc.handler.PINGRequestMessageHandler;
import com.lxy.rpc.handler.RpcRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MainServer implements HotDeployment, InitializingBean {

    @Autowired
    private LXYHotDeploymentConfig lxyHotDeploymentConfig;

    private void serverInit(){
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcRequestMessageHandler RPC_HANDLER = new RpcRequestMessageHandler();
        PINGRequestMessageHandler PING_HANDLER = new PINGRequestMessageHandler();


        int readerIdleIimeSeconds =
                lxyHotDeploymentConfig.<Integer>getProperty(ConstEnum.service_heartbeat_readerIdleIimeSeconds.getName());

        int writeIdleTimeSeconds =
                lxyHotDeploymentConfig.<Integer>getProperty(ConstEnum.service_heartbeat_writeIdleTimeSeconds.getName());

        int alldleTimeSeconds =
                lxyHotDeploymentConfig.<Integer>getProperty(ConstEnum.service_heartbeat_alldleTimeSeconds.getName());

        Integer port = lxyHotDeploymentConfig.<Integer>getProperty(ConstEnum.service_regist_port.getName());


        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(new IdleStateHandler(
                            readerIdleIimeSeconds,
                            writeIdleTimeSeconds,
                            alldleTimeSeconds));
                    // ChannelDuplexHandler 可以同时作为入站和出站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // 用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了读空闲事件
                            if (event.state() == IdleState.READER_IDLE) {
                                log.debug("已经 "+readerIdleIimeSeconds+"s 没有读到数据了");
                                ctx.channel().close();
                            }
                        }
                    });
                    ch.pipeline().addLast(RPC_HANDLER);
                    ch.pipeline().addLast(PING_HANDLER);
                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                    // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件

                }
            });

            Channel channel = serverBootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private Runnable serverRunner = new Runnable() {
        @Override
        public void run() {
            serverInit();
        }
    };

    public void init(){
        new Thread(serverRunner).start();
    }

    @Override
    public void doHotDeploy() {
        init();
    }

    @Override
    public void doRegistInConfig() {
        this.lxyHotDeploymentConfig.registInHotDeplayQueue(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
