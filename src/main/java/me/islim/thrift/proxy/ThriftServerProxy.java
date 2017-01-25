package me.islim.thrift.proxy;


import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Constructor;

public class ThriftServerProxy {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private int port;// 端口
    private String serviceInterface;// 实现类接口
    private Object serviceImplObject;// 实现类

    public void start(){
        new Thread(()->{
            try {
                TServerSocket serverTransport = new TServerSocket(getPort());

                Class Processor = Class.forName(getServiceInterface() + "$Processor");

                Class Iface = Class.forName(getServiceInterface() + "$Iface");

                Constructor con = Processor.getConstructor(Iface);

                TProcessor processor = (TProcessor) con.newInstance(serviceImplObject);
                TBinaryProtocol.Factory protFactory = new TBinaryProtocol.Factory(true, true);
                TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
                args.protocolFactory(protFactory);
                args.processor(processor);
                TServer server = new TThreadPoolServer(args);
                logger.info("Starting server on port " + getPort() + " ...");
                server.serve();
            } catch (TTransportException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public Object getServiceImplObject() {
        return serviceImplObject;
    }

    public void setServiceImplObject(Object serviceImplObject) {
        this.serviceImplObject = serviceImplObject;
    }



    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:proxy/applicationContext-server.xml");
        ThriftServerProxy thriftServerProxy = (ThriftServerProxy) context.getBean(ThriftServerProxy.class);
        thriftServerProxy.start();
    }
}
