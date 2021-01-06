package com.example.crossds.controller;

import com.example.crossds.model.Credentials;
import com.example.crossds.model.User;
import com.example.crossds.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class UserController extends MainController{

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Gets the User given its id
     * Update the access_token if necessary
     * @param id the id
     * @return the user
     */
    public User getUser(Long id){
        Optional<User> opt = userRepository.findById(id);
        if(opt.isPresent()){
            User user = opt.get();
            //update access_token if necessary
            return updateCredentials(user);;
        }else{
            throw new NoSuchElementException();
        }
    }

    public User updateCredentials(User user){
        if(user.getCredentials().willSoonExpire()){
            Credentials credentials = getApiService(user.getPlatform()).refreshTokens(user.getCredentials());
            logger.info("API CALL : "+ user.getPlatform().getName()+" -> refreshTokens");
            user.setCredentials(credentials);
            userRepository.save(user);
        }
        return user;
    }

    public String updateCredntialsAndGetAccessToken(User user) {
        return updateCredentials(user).getCredentials().getAccess_token();
    }
}
