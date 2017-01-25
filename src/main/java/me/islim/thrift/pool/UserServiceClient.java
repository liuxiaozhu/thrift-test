package me.islim.thrift.pool;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class UserServiceClient {
    @Autowired
    private ConnectionManager connectionManager;

    public void start() {
        try {
            TProtocol protocol = new TBinaryProtocol(connectionManager.getSocket());
            UserService.Client client = new UserService.Client(protocol);
            UserRequest request = new UserRequest();
            request.setId("10000");
            UserResponse urp = client.userInfo(request);
            if (urp.code != null && !urp.code.equals("")) {
                System.out.println("返回代码：" + urp.code + "; 参数是：" + urp.params.get("name"));
            }
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:pool/applicationContext-client.xml");
        UserServiceClient userServiceClient = (UserServiceClient) context.getBean(UserServiceClient.class);
        userServiceClient.start();
    }
}
