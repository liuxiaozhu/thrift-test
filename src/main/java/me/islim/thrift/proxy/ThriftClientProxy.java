package me.islim.thrift.proxy;


import me.islim.thrift.Test;
import me.islim.thrift.pool.ConnectionManager;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ThriftClientProxy {
    private ConnectionManager connectionManager;

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Object getClient(Class clazz) {
        Object result = null;
        try {
            TTransport transport = connectionManager.getSocket();
            TProtocol protocol = new TBinaryProtocol(transport);
            Class client = Class.forName(clazz.getName() + "$Client");
            Constructor con = client.getConstructor(TProtocol.class);
            result = con.newInstance(protocol);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) throws TException {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:proxy/applicationContext-client.xml");
        ThriftClientProxy thriftClientProxy = (ThriftClientProxy) context.getBean(ThriftClientProxy.class);
        Test.Iface client = (Test.Iface)thriftClientProxy.getClient(Test.class);
        client.ping(2017);
    }
}
