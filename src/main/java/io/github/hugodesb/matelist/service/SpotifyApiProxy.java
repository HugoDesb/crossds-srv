package io.github.hugodesb.matelist.service;

import io.github.hugodesb.matelist.controller.UserController;
import io.github.hugodesb.matelist.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SpotifyApiProxy extends ApiCallProxy implements InvocationHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(ApiCallProxy.class);

    private final Map<String, Method> methods = new HashMap<>();

    @Autowired
    private UserController userController;

    public SpotifyApiProxy(@Qualifier("spotifyApiService") SpotifyApiService target) {
        super(target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {

        //update user
        //keep on

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




        long start = System.nanoTime();
        Object result = methods.get(method.getName()).invoke(target, args);
        long elapsed = System.nanoTime() - start;
        System.out.println("Executing "+method.getName()+" finished in "+elapsed+" ns");
        return result;
    }
}
