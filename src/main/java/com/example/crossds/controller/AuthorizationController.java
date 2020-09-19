package com.example.crossds.controller;

import com.example.crossds.business.Account;
import com.example.crossds.business.Platform;
import com.example.crossds.repository.AccountRepository;
import com.example.crossds.service.ApiResponseWrapper;
import com.example.crossds.service.Credentials;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.util.List;

@org.springframework.stereotype.Controller
@RequestMapping("/oauth")
@CrossOrigin
public class AuthorizationController extends MainController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/{platform}")
    public @ResponseBody URI getOAuthUrl(@PathVariable String platform){

        Platform p = getPlatform(platform);

        if(p==null){
            throw new IllegalArgumentException("platform unknown");
        }

        return getApiService(p).getOAuthUrl();
    }

    @GetMapping(path="/{platformValue}/callback")
    public RedirectView callback(@PathVariable String platformValue,
                                 @RequestParam String code,
                                 @RequestParam(required=false) String error_reason,
                                 @RequestParam(required = false) String state) throws Exception {

        URIBuilder ret = new URIBuilder("http://localhost:4200/");
        Platform platform = getPlatform(platformValue);


        if (platform == null) {
            throw new IllegalArgumentException("platform unknown");
        }

        // state for spotify doesn't match
        if (state != null){
            if(!state.equals("x4xkmn9pu3j6ukrs8n")){
                throw new Exception();
            }
        }

        // deezer service has an error
        if (error_reason != null) {
            throw new Exception();
        }

        try {
            Credentials credentials = getApiService(platform).getTokens(code);
            Account distantAccount;

            ApiResponseWrapper<Account> responseAccount = getApiService(platform).getCurrentAccount(credentials);
            credentials = responseAccount.getCredentials();
            distantAccount = responseAccount.getData();
            distantAccount.setCredentials(credentials);

            //Search if the distantAccount already exists in db
            List<Account> accounts = accountRepository.findOneByPlatformAndUsername(platform, distantAccount.getUsername());

            if (accounts.size() == 1) {
                //The distantAccount already exists, let's update a few fields and save it
                Account existingAccount = accounts.get(0);
                existingAccount.setDisplayName(distantAccount.getDisplayName());
                existingAccount.setCredentials(credentials);
                existingAccount.setPicture_url(distantAccount.getPicture_url());
                existingAccount.setEmail(distantAccount.getEmail());
                accountRepository.save(existingAccount);

                ret.addParameter("id", existingAccount.getId_account().toString());
                return new RedirectView(ret.build().toString());
            }else{
                accountRepository.save(distantAccount);
                ret.addParameter("id", distantAccount.getId_account().toString());

            }

        }catch (Exception e){
            e.printStackTrace();
            throw new Exception();
        }
        return new RedirectView(ret.build().toString());
    }
}
