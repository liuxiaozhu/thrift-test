package me.islim.thrift;


import me.islim.thrift.impl.TestImpl;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

public class Server {

    private static final int PORT = 1234;

    public void startServer(){
        try{
            TServerSocket serverTransport = new TServerSocket(PORT);

            Test.Processor process = new Test.Processor(new TestImpl());

            TBinaryProtocol.Factory portFactory = new TBinaryProtocol.Factory(true, true);

            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
            args.processor(process);
            args.protocolFactory(portFactory);

            TServer server = new TThreadPoolServer(args);
            server.serve();
        }catch (TTransportException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Server server = new Server();
        server.startServer();
    }
}
