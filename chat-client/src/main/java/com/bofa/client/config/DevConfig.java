package com.bofa.client.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ConsoleCommandManager;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.sql.DataSource;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.config
 * @date 2019/4/17
 */

@Configuration
@ComponentScan(basePackages = "com.bofa")
public class DevConfig {

    static final Logger logger = LoggerFactory.getLogger(DevConfig.class);

    /**
     * mybatis配置, DAO扫描
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        logger.info("mapper scanner");
        MapperScannerConfigurer mapperConfig = new MapperScannerConfigurer();
        mapperConfig.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperConfig.setAnnotationClass(Mapper.class);
        mapperConfig.setBasePackage("com.bofa");
        return mapperConfig;
    }

    /**
     * 注册commandHandler到容器后
     * 缓存到ConsoleCommandManager的map
     * 放弃该方法
     * 选择用spring-beans提供的InitializingBean接口实现方案来替代
     *
     * @return
     * @see BaseConsoleCommand
     * 该抽象类实现了InitializingBean的afterPropertiesSet方法实现
     * 初始化cmpt后将cmpt引用缓存在ConsoleCommandManager里，再放入beanFactory
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory
     * 对应的method invokeInitMethods
     * init-method是在afterPropertiesSet之后执行的
     * bean的init-method指定的方法是通过反射执行的，虽然效率低，但是消除了对spring的依赖
     */
//    @Bean
//    public BeanFactoryPostProcessor commandHandler() {
//        return (ConfigurableListableBeanFactory var1) -> {
//            for (String beanName : var1.getBeanDefinitionNames()) {
//                if (beanName.matches(".*?CommandHandler$")) {
//                    BaseConsoleCommand bean = (BaseConsoleCommand) var1.getBean(beanName);
//                    ConsoleCommandManager.putCommandHandler(bean.getCommand(), bean);
//                }
//            }
//        };
//    }

    /**
     * BeanFactoryPostProcessor 对beanDefinition进行处理，在bean实例化注册到容器之前执行.
     * BeanPostProcessor 在bean实例化后注册到容器前后执行
     */
//    @Bean(name = "handlerScanner")
//    public BeanFactoryPostProcessor handlerScannerConfigure() throws IOException, ClassNotFoundException {
//        return (ConfigurableListableBeanFactory var1) -> {
//            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//            String resourcePath = ClassUtils.convertClassNameToResourcePath("com.bofa.client.handler");
//            String resourcePattern = "/*.class";
//            try {
//                Resource[] resources = resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourcePath + resourcePattern);
//                CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
//                for (Resource r : resources) {
//                    String className = factory.getMetadataReader(r).getClassMetadata().getClassName();
//                    Class<?> clazz = Class.forName(className);
//                    if (FieldUtils.getFieldsWithAnnotation(clazz, Autowired.class).length != 0) {
//                        Field autoWiredField = FieldUtils.getFieldsWithAnnotation(clazz, Autowired.class)[0];
//                        Object instance = FieldUtils.readStaticField(clazz.getDeclaredField("INSTANCE"), true);
//                        FieldUtils.writeField(autoWiredField, instance, var1.getBean(autoWiredField.getName()));
//                    }
//                }
//            } catch (IOException | ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
//                e.printStackTrace();
//            }
//        };
//    }

}
