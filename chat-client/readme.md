# Chat-netty client-project

### 技术设计: 
1. 没有客户端的开发经验，因此采用原生终端代替客户端
2. 客户端要考虑对指令的封装设计指令处理器
   对处理器的缓存封装和业务对象装配
   这里利用Spring的InitializingBean扩展接口
   对commandHandler对象注册到容器后并缓存到ConsoleCommandManager中
   
   然而之前实现方案思路是自定义[BeanFactoryPostProcessor]
   获取bean对象缓存到ConsoleCommandManager, 参阅DevConfig类中被注释的commandHandler方法
   但是据debug了解到，BeanFactoryPostProcessor 是对[beanDefinition]进行处理
   此时bean对象还未实例化注册到beanFactory中，debug [beanFactory.alreadyBeanCreated]可以看到
   如果这时候强制获取beanFactory.getBean(String beanName)
   会debug到 [DefaultSingletonBeanRegistry的getSingleton]，强行注册一个bean对象，这样的话业务对象就没办法装配到该对象中
   spring 源码中对beanFactoryPostProcessor这么解释道.
   
   [may interact with and modify bean definitions, but never bean instances.
   Doing so may cause premature bean instantiation, violating the container and causing unintended side-effects.
   if bean instance interaction is required, consider implementing.]
   
   [AbstractApplicationContext类refresh方法调用的finishBeanFactoryInitialization方法
   执行bean的实例化、成员变量装配注入、初始化工作]
   ->
   [beanFactory.preInstantiateSingletons()]
   ->
   [AbstractAutowireCapableBeanFactory.doCreateBean调用createBeanInstance方法] 构造bean对象
   ->
   [doCreateBean调用populateBean] 对其成员变量进行装配注入
   
   bean对象的构造实例化 -> 依赖对象的自动装配 -> bean对象的初始化操作(InitializingBean接口或者init-method)
   (如果bean对象已存在，会跳过AbstractAutowireCapableBeanFactory类createBeanInstance方法对bean对象的装配过程)
   
   因此，该实现方案不成立. 
   再次强调原作者的文档注释
   [may interact with and modify bean definitions, but never bean instances.
   Doing so may cause premature bean instantiation, violating the container and causing unintended side-effects.
   if bean instance interaction is required, consider implementing.]
      
   采用InitializingBean接口执行bean对象的初始化操作(缓存到manager)
3. 客户端采用H2 Embedded 嵌入式数据库实现对用户聊天缓存以及通知缓存， 以及Mybatis作为ORM框架
   netty InboundHandler 需要引入业务对象，因此也实现了Spring的扩展接口
   内容篇幅过大，本人较懒, 详细参阅NettyHandlerCmpt
   