package me.islim.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class Client {

    private static final String HOST = "localhost";

    private static final int PORT = 1234;

    public static void main(String[] args) throws TException {

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
}
