package me.islim.thrift.impl;

import me.islim.thrift.Test;
import org.apache.thrift.TException;


public class TestImpl implements Test.Iface {
    @Override
    public void ping(int length) throws TException {
        System.out.println("calling ping ,length=" + length);
    }
}
