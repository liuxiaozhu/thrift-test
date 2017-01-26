package me.islim.thrift.zk.server.impl;


import me.islim.thrift.zk.EchoService;
import org.apache.thrift.TException;

public class EchoServiceImpl implements EchoService.Iface {

    @Override
    public String echo(String msg) throws TException {
        return "server :" + msg;
    }
}