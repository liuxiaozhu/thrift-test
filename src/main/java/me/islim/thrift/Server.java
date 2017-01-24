package me.islim.thrift;


import me.islim.thrift.impl.TestImpl;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.*;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * Underlying I/O: 底层传输
 * Transport: 传输层
 * protocol: 协议层
 * Processor: 处理层
 * Server: 服务层
 * Your Code: 业务逻辑层
 */
public class Server {

    private static final int PORT = 1234;

    /**
     * 多线程阻塞式IO服务模型-TThreadPoolServer
     */
    public static void startThreadPoolServer() {

        Test.Processor process = new Test.Processor<Test.Iface>(new TestImpl());

        TServerSocket serverTransport = null;
        try {
            serverTransport = new TServerSocket(PORT);
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        TBinaryProtocol.Factory portFactory = new TBinaryProtocol.Factory(true, true);

        TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
        args.processor(process);
        args.protocolFactory(portFactory);

        TServer server = new TThreadPoolServer(args);
        server.serve();
    }

    /**
     * 多线程半同步半异步的服务模型-TThreadedSelectorServer
     */
    public static void startThreadedSelectorServer(){
        TProcessor tprocessor = new Test.Processor<Test.Iface>(new TestImpl());

        TNonblockingServerSocket serverTransport = null;
        try {
            serverTransport = new TNonblockingServerSocket(PORT);
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
        tArgs.processor(tprocessor);
        tArgs.transportFactory(new TFramedTransport.Factory());

        tArgs.protocolFactory(new TBinaryProtocol.Factory());

        TServer server = new TThreadedSelectorServer(tArgs);
        server.serve();
    }

    /**
     * 半同步半异步的服务模型-THsHaServer（异步调用客户端）
     */
    public static void startHsHaServer(){
        TProcessor tprocessor = new Test.Processor<Test.Iface>(new TestImpl());
        TNonblockingServerSocket serverTransport = null;
        try {
            serverTransport = new TNonblockingServerSocket(PORT);
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        THsHaServer.Args tArgs = new THsHaServer.Args(serverTransport);
        tArgs.processor(tprocessor);
        tArgs.transportFactory(new TFramedTransport.Factory());
        tArgs.protocolFactory(new TBinaryProtocol.Factory());

        TServer server = new THsHaServer(tArgs);
        server.serve();
    }

    /**
     * 非阻塞式IO服务模型-TNonblockingServer
     */
    public static void startNonblockingServer() {

        Test.Processor process = new Test.Processor<Test.Iface>(new TestImpl());

        TNonblockingServerSocket serverTransport = null;
        try {
            serverTransport = new TNonblockingServerSocket(PORT);
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        TNonblockingServer.Args tArgs = new TNonblockingServer.Args(serverTransport);
        tArgs.processor(process);
        tArgs.transportFactory(new TFramedTransport.Factory());
        tArgs.protocolFactory(new TCompactProtocol.Factory());

        TServer server = new TNonblockingServer(tArgs);
        server.serve();
    }

    public static void main(String[] args) {
//        startThreadPoolServer();
//        startNonblockingServer();
//        startHsHaServer();
        startThreadedSelectorServer();
    }
}
