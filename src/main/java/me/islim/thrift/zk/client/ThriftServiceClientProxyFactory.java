package me.islim.thrift.zk.client;

import me.islim.thrift.zk.client.ThriftClientPoolFactory;
import me.islim.thrift.zk.client.ThriftServerAddressProvider;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ThriftServiceClientProxyFactory implements FactoryBean, InitializingBean {
    private Integer maxActive = 32;// 最大活跃连接数

    // ms,default 3 min,链接空闲时间
    // -1,关闭空闲检测
    private Integer idleTime = 180000;
    private ThriftServerAddressProvider serverAddressProvider;

    private Object proxyClient;
    private Class<?> objectClass;

    private GenericObjectPool<TServiceClient> pool;

    private ThriftClientPoolFactory.PoolOperationCallBack callback = new ThriftClientPoolFactory.PoolOperationCallBack() {
        @Override
        public void make(TServiceClient client) {
            System.out.println("create");
        }

        @Override
        public void destroy(TServiceClient client) {
            System.out.println("destroy");
        }
    };

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public void setIdleTime(Integer idleTime) {
        this.idleTime = idleTime;
    }

    public void setServerAddressProvider(ThriftServerAddressProvider serverAddressProvider) {
        this.serverAddressProvider = serverAddressProvider;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // 加载Iface接口
        objectClass = classLoader.loadClass(serverAddressProvider.getService() + "$Iface");
        // 加载Client.Factory类
        Class<TServiceClientFactory<TServiceClient>> fi = (Class<TServiceClientFactory<TServiceClient>>) classLoader.loadClass(serverAddressProvider.getService() + "$Client$Factory");
        TServiceClientFactory<TServiceClient> clientFactory = fi.newInstance();
        ThriftClientPoolFactory clientPool = new ThriftClientPoolFactory(serverAddressProvider, clientFactory, callback);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMinIdle(0);
        poolConfig.setMinEvictableIdleTimeMillis(idleTime);
        poolConfig.setTimeBetweenEvictionRunsMillis(idleTime / 2L);
        pool = new GenericObjectPool<TServiceClient>(clientPool, poolConfig);
        proxyClient = Proxy.newProxyInstance(classLoader, new Class[] { objectClass }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //
                TServiceClient client = pool.borrowObject();
                try {
                    return method.invoke(client, args);
                } catch (Exception e) {
                    throw e;
                } finally {
                    pool.returnObject(client);
                }
            }
        });
    }

    @Override
    public Object getObject() throws Exception {
        return proxyClient;
    }

    @Override
    public Class<?> getObjectType() {
        return objectClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void close() {
        if (serverAddressProvider != null) {
            serverAddressProvider.close();
        }
    }
}
