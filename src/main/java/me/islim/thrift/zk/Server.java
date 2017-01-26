package me.islim.thrift.zk;


import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {
    public static void main(String[] args) {
        try {
            new ClassPathXmlApplicationContext("classpath:zk/spring-context-thrift-server.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
