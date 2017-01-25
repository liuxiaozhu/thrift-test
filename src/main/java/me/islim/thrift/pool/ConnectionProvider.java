package me.islim.thrift.pool;


import org.apache.thrift.transport.TSocket;

public interface ConnectionProvider {
    TSocket getConnection();
    void returnCon(TSocket socket);
}
