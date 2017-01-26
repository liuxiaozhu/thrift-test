package me.islim.thrift.zk.server;


public interface ThriftServerIpResolve {
    String getServerIp() throws Exception;

    void reset();

    //当IP变更时,将会调用reset方法
    static interface IpRestCalllBack{
        public void rest(String newIp);
    }
}
