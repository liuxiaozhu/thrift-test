package me.islim.thrift.demo.pool;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserServiceServer {
    private int servicePort;

    @Autowired
    private UserService.Iface iface;

    public void start() {
        try {
            TServerSocket serverTransport = new TServerSocket(servicePort);

            TProcessor processor = new UserService.Processor<UserService.Iface>(iface);

            TBinaryProtocol.Factory proFactory = new TBinaryProtocol.Factory(true, true);
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
            args.processor(processor);
            args.protocolFactory(proFactory);

            TServer server = new TThreadPoolServer(args);

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-server.xml");
        UserServiceServer userServiceServer = context.getBean(UserServiceServer.class);
        userServiceServer.start();
    }
}
