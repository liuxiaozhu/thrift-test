package me.islim.thrift.zk.server;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;

public class ZookeeperFactory implements FactoryBean<CuratorFramework> {

    private String zkHosts;
    // session超时
    private int sessionTimeout = 30000;
    private int connectionTimeout = 30000;

    // 共享一个zk链接
    private boolean singleton = true;

    // 全局path前缀,常用来区分不同的应用
    private String namespace;

    private final static String ROOT = "rpc";

    private CuratorFramework zkClient;

    public String getZkHosts() {
        return zkHosts;
    }

    public void setZkHosts(String zkHosts) {
        this.zkHosts = zkHosts;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public static String getROOT() {
        return ROOT;
    }

    public CuratorFramework getZkClient() {
        return zkClient;
    }

    public void setZkClient(CuratorFramework zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public CuratorFramework getObject() throws Exception {
        if (singleton) {
            if (zkClient == null) {
                zkClient = create();
                zkClient.start();
            }
            return zkClient;
        }
        return create();
    }

    @Override
    public Class<?> getObjectType() {
        return CuratorFramework.class;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    public CuratorFramework create() throws Exception {
        if (StringUtils.isEmpty(namespace)) {
            namespace = ROOT;
        } else {
            namespace = ROOT +"/"+ namespace;
        }
        return create(zkHosts, sessionTimeout, connectionTimeout, namespace);
    }

    public static CuratorFramework create(String connectString, int sessionTimeout, int connectionTimeout, String namespace) {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        return builder.connectString(connectString)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(30000)
                .canBeReadOnly(true)
                .namespace(namespace)
                .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                .defaultData(null)
                .build();
    }

    public void close() {
        if (zkClient != null) {
            zkClient.close();
        }
    }
}
