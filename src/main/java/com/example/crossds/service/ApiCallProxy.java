package com.example.crossds.service;

import com.example.crossds.controller.UserController;
import com.example.crossds.model.User;
import com.example.crossds.service.genericapi.GenericApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component("apiCallProxy")
public class ApiCallProxy implements InvocationHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(ApiCallProxy.class);

    private final Map<String, Method> methods = new HashMap<>();

    @Autowired
    private UserController userController;

    // this object is a Generic API type
    protected final GenericApiService target;

    public ApiCallProxy(@Qualifier("genericApiService") GenericApiService target) {
        this.target = target;

        for(Method method: target.getClass().getDeclaredMethods()) {
            this.methods.put(method.getName(), method);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {

        // si le premier arg is of type User --> refresh if needed the tokens
        if(args.length != 0 && args[0] instanceof User){
            User user = (User) args[0];

            //if necessary updates the user's credentials
            if(user.getCredentials().willSoonExpire()){
                user = userController.updateCredentials(user);
            }

            // Wait if the limit of api calls per second have been reached

            LOGGER.info("Call {} for {}", method.getName(), user.getPlatform().getName());
            Object result = methods.get(method.getName()).invoke(target, args);
            return result;
        }
    }
}
