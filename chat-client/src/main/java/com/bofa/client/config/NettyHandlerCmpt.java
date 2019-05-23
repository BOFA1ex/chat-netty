package com.bofa.client.config;

import com.bofa.client.console.ConsoleCommandManager;
import com.bofa.codeC.PacketCodeHandler;
import com.bofa.codeC.Spliter;
import com.bofa.protocol.request.AbstractRequestPacket;
import com.bofa.protocol.request.ClientCloseRequestPacket;
import com.bofa.util.LocalDateTimeUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.config
 * @date 2019/4/22
 */
@Component
public class NettyHandlerCmpt implements BeanFactoryPostProcessor, BeanPostProcessor, ApplicationListener {

    private static Bootstrap bootstrap = new Bootstrap();
    private static List<ChannelHandler> channelHandlers = new LinkedList<>();
    static List<String> handleBeanClassNames = new ArrayList<>();

    static final Logger logger = LoggerFactory.getLogger(NettyHandlerCmpt.class);

    private static final int MAX_RETRY = 5;
    private static int handlerOrder = 0;
    private static final ThreadPoolExecutor SINGLE_CONSOLE_POOL;
    private static final String DEFAULT_BASE_PACKAGES = "com.bofa.client.handler";
    static {
        SINGLE_CONSOLE_POOL = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadFactoryBuilder().setNameFormat("console-worker").build());
    }

    /**
     * configuration bootStrap
     * set channel type, option
     */
    public NettyHandlerCmpt() {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        bootstrap
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
    }


    /**
     * access host and port in application.properties by ApplicationContextCmpt
     * when listened ContextRefreshedEvent, bootStrap connect it with host and port.
     * start console-worker thread and application hook-thread.
     *
     * @param retry
     */
    private void connect(int retry) {
        PropertiesValueConfig valueConfig = (PropertiesValueConfig) ApplicationContextCmpt.getBean("propertiesValueConfig");
        String host = valueConfig.getHost();
        int port = valueConfig.getPort();
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                Channel channel = ((ChannelFuture) future).channel();
                System.out.println(LocalDateTimeUtil.now() + " 连接成功");
                startConsoleThread(channel);
                registerHook(channel);
            } else if (retry == 0) {
                System.out.println("retry is empty");
            } else {
                int order = MAX_RETRY - retry + 1;
                int delay = 1 << order;
                System.err.println(LocalDateTimeUtil.now() + " 连接失败, 第" + order + "次重新连接");
                bootstrap.config().group().schedule(() -> connect(retry - 1)
                        , delay, TimeUnit.SECONDS);
            }
        });
    }

    private void registerHook(Channel channel) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("channel: " + channel);
            try {
                channel.writeAndFlush(new ClientCloseRequestPacket()).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * 开启控制台线程
     * 由ConsoleCommandManager判断执行输入的指令
     *
     * @param channel
     * @see ConsoleCommandManager
     */
    private static void startConsoleThread(Channel channel) {
        SINGLE_CONSOLE_POOL.execute(() -> {
            while (true) {
                try {
                    ConsoleCommandManager.execute(channel);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }


    /**
     * channelScanner 扫描结束后执行回调事件
     * 将channelHandlers 封装到SocketChannel的pipeline链中
     */
    public static void channelScannerCallBack() {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new Spliter());
                ch.pipeline().addLast(PacketCodeHandler.INSTANCE);
                channelHandlers.forEach(ch.pipeline()::addLast);
            }
        });
    }

    /**
     * @param factory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        HandlerScanner scanner = new HandlerScanner((BeanDefinitionRegistry) factory);
        scanner.setResourceLoader(ApplicationContextCmpt.getApplicationContext());
        logger.info("Inbound handler scanner");
        scanner.doScan(DEFAULT_BASE_PACKAGES);
    }

    /**
     * 容器初始化结束事件触发bootStrap连接回调事件
     *
     * @param applicationEvent
     */
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            channelScannerCallBack();
            connect(MAX_RETRY);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (handleBeanClassNames.size() == handlerOrder) {
            return bean;
        }
        handleBeanClassNames.forEach(
                beanName0 -> {
                    if (beanName0.equals(beanName)) {
                        channelHandlers.add((ChannelHandler) bean);
                        handlerOrder++;
                    }
                }
        );
        return bean;
    }

    public static class HandlerScanner extends ClassPathBeanDefinitionScanner {

        public HandlerScanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        static final Class<? extends Annotation> DEFAULT_FILTER = ChannelHandler.Sharable.class;

        @Override
        protected void registerDefaultFilters() {
            this.addIncludeFilter(new AnnotationTypeFilter(DEFAULT_FILTER));
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
            beanDefinitionHolders.forEach(
                    beanDefinitionHolder -> {
                        String beanName = beanDefinitionHolder.getBeanName();
                        logger.info("scan handle bean className : " + beanName);
                        handleBeanClassNames.add(beanName);
                    }
            );
            return beanDefinitionHolders;
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return super.isCandidateComponent(beanDefinition) && beanDefinition.getMetadata()
                    .hasAnnotation(DEFAULT_FILTER.getName());
        }
    }
}
