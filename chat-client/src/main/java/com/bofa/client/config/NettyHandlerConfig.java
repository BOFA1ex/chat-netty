//package com.bofa.client.config;
//
//import com.bofa.client.handler.ChatClientHandler;
//import com.bofa.codeC.PacketCodeHandler;
//import com.bofa.codeC.Spliter;
//import com.google.common.util.concurrent.ThreadFactoryBuilder;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import org.apache.commons.lang3.reflect.FieldUtils;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
//import org.springframework.util.ClassUtils;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.util.*;
//import java.util.concurrent.LinkedBlockingDeque;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.function.UnaryOperator;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * @author Bofa
// * @version 1.0
// * @description com.bofa.client.config
// * @date 2019/4/21
// */
//@Configuration
//public class NettyHandlerConfig {
//
//    /**
//     * 处理ResponseHandler的autowire装配
//     * @see com.bofa.client.handler.ChatClientHandler
//     * 由于该Handler封装了所有的ResponseInboundHandler
//     * ResponseInboundHandler 不能考虑加载到容器，因此没办法被ComponentScan扫描并自动装配Autowired依赖
//     * 因此，考虑自定义BeanPostProcessor, 之前考虑自定义BeanFactoryPostProcessor
//     * 后来发现MapperScannerConfigurer 只是把mapper接口注册进容器，但是其实现类还是需要Component
//     * @return
//     * @throws IOException
//     */
////    @Bean
////    public BeanPostProcessor handlerScannerConfigure() throws IOException {
////        System.out.println("handler scanner");
////        return new BeanPostProcessor() {
////
////            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
////            String resourcePath = ClassUtils.convertClassNameToResourcePath("com.bofa.client.handler");
////            String resourcePattern = "/*.class";
////            CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
////            Map<Field, Class> map = Stream.of(resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourcePath + resourcePattern))
////                    .map(resource -> {
////                        try {
////                            String className = factory.getMetadataReader(resource).getClassMetadata().getClassName();
////                            Class<?> clazz = Class.forName(className);
////                            if (FieldUtils.getFieldsWithAnnotation(clazz, Autowired.class).length != 0) {
////                                return clazz;
////                            }
////                        } catch (IOException | ClassNotFoundException e) {
////                            e.printStackTrace();
////                        }
////                        return null;
////                    })
////                    .filter(Objects::nonNull)
////                    .collect(Collectors.toMap((Class v) -> FieldUtils.getFieldsWithAnnotation(v, Autowired.class)[0], UnaryOperator.identity()
////                    ));
////
////            @Override
////            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
////                return bean;
////            }
////
////            @Override
////            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
////                map.keySet().forEach(field -> {
////                    if (field.getName().equals(beanName)) {
////                        map.get(field);
////                        try {
////                            Object instance = FieldUtils.readStaticField(map.get(field).getDeclaredField("INSTANCE"), true);
////                            FieldUtils.writeField(field, instance, bean, true);
////                        } catch (IllegalAccessException | NoSuchFieldException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                });
////                return bean;
////            }
////        };
////    }
//}
