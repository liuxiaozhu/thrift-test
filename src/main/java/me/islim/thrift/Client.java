package me.islim.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final String HOST = "localhost";

    private static final int PORT = 1234;

    private static final int TIMEOUT = 30000;

    public static void main(String[] args) throws TException {
//        startThreadPoolClient();
//        startNonblockingClient();
        startAsyncClient();
    }

    public static void startThreadPoolClient(){
        TTransport transport = new TSocket(HOST, PORT);

        TProtocol protocol = new TBinaryProtocol(transport);

        Test.Client client = new Test.Client(protocol);

        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        try {
            client.ping(2017);
        } catch (TException e) {
            e.printStackTrace();
        }
        transport.close();
    }

    public static void startNonblockingClient(){
        TTransport transport = new TFramedTransport(new TSocket(HOST, PORT, TIMEOUT));

        TProtocol protocol = new TCompactProtocol(transport);

        Test.Client client = new Test.Client(protocol);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        try {
            client.ping(2017);
        } catch (TException e) {
            e.printStackTrace();
        }
        transport.close();
    }

    public static void startAsyncClient(){
        //异步调用管理器
        TAsyncClientManager clientManager = null;
        try {
            clientManager = new TAsyncClientManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //设置传输通道，调用非阻塞IO
        TNonblockingTransport transport = null;
        try {
            transport = new TNonblockingSocket(HOST, PORT, TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 协议要和服务端一致
        //TProtocolFactory tprotocol = new TCompactProtocol.Factory();
        TProtocolFactory tprotocol = new TBinaryProtocol.Factory();

        Test.AsyncClient asyncClient = new Test.AsyncClient(tprotocol, clientManager, transport);
        CountDownLatch latch = new CountDownLatch(1);
        AsyncMethodCallback callBack = new AsynCallback(latch);
        // 调用服务
        try {
            asyncClient.ping(2017, callBack);
        } catch (TException e) {
            e.printStackTrace();
        }
        //等待完成异步调用
        boolean wait = false;
        try {
            wait = latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class AsynCallback implements AsyncMethodCallback<Test.AsyncClient.ping_call> {
    private CountDownLatch latch;

    public AsynCallback(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onComplete(Test.AsyncClient.ping_call response) {
        try {
            // response is null
            if(response != null)
                System.out.println("AsynCall result :" + response.getResult().toString());
        } catch (TException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    @Override
    public void onError(Exception exception) {
        System.out.println("onError :" + exception.getMessage());
        latch.countDown();
    }
}