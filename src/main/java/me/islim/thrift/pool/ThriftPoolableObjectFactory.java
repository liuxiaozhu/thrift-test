package me.islim.thrift.pool;


import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftPoolableObjectFactory implements PooledObjectFactory<TTransport> {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    /** 服务的IP */
    private String serviceIP;
    /** 服务的端口 */
    private int servicePort;
    /** 超时设置 */
    private int timeOut;

    public ThriftPoolableObjectFactory(String serviceIP, int servicePort, int timeOut) {
        super();
        this.serviceIP = serviceIP;
        this.servicePort = servicePort;
        this.timeOut = timeOut;
    }

    @Override
    public PooledObject<TTransport> makeObject() throws Exception {
        try {
            TTransport transport = new TSocket(this.serviceIP, this.servicePort, this.timeOut);
            transport.open();
            PooledObject<TTransport> object = new DefaultPooledObject<>(transport);
            return object;
        } catch (Exception e) {
            logger.error("error ThriftPoolableObjectFactory()", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroyObject(PooledObject<TTransport> pooledObject) throws Exception {
        if (pooledObject.getObject().isOpen()) {
            pooledObject.getObject().close();
        }

    }

    /**
     * 检验对象是否可以由pool安全返回
     */
    @Override
    public boolean validateObject(PooledObject<TTransport> pooledObject) {
        try {
            if (pooledObject.getObject() instanceof TSocket) {
                TSocket thriftSocket = (TSocket) pooledObject.getObject();
                if (thriftSocket.isOpen()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 激活对象
     */
    @Override
    public void activateObject(PooledObject<TTransport> pooledObject) throws Exception {

    }

    /**
     * 使无效 以备后用
     */
    @Override
    public void passivateObject(PooledObject<TTransport> pooledObject) throws Exception {

    }
}
