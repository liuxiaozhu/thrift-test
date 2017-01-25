package me.islim.thrift.pool.impl;

import com.google.common.collect.Maps;
import me.islim.thrift.pool.UserRequest;
import me.islim.thrift.pool.UserResponse;
import me.islim.thrift.pool.UserService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService.Iface {
    @Override
    public UserResponse userInfo(UserRequest request) {
        try {
            UserResponse response = new UserResponse();
            response.setCode("0");
            Map<String,String> params= Maps.newHashMap();
            params.put("name", "lucy");
            response.setParams(params);
            System.out.println("接收参数是：id="+request.id);
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
