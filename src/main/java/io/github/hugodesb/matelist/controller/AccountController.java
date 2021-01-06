package io.github.hugodesb.matelist.controller;

import io.github.hugodesb.matelist.model.Account;
import io.github.hugodesb.matelist.model.AccountPlaylist;
import io.github.hugodesb.matelist.controller.reponse.AccountResponse;
import io.github.hugodesb.matelist.controller.reponse.exceptions.AccountNotFoundException;
import io.github.hugodesb.matelist.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/user")
@CrossOrigin
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/{id}")
    public @ResponseBody
    AccountResponse getAccountInfos(@PathVariable("id") String id){
        Long i = Long.parseLong(id);
        Optional<Account> a = accountRepository.findById(i);
        if(a.isPresent()){
            return new AccountResponse(a.get());
        }else{
            throw new AccountNotFoundException(i);
        }
    }

    /**
     * @// TODO: 24/07/2020 NOT DONE
     * @param id
     */
    @GetMapping("/{userid}/playlists")
    public @ResponseBody void getAccountPlaylists(@PathVariable("userid") String id){
        Long i = Long.parseLong(id);
        Optional<Account> a = accountRepository.findById(i);
        if(a.isPresent()){
            Account account = a.get();
            Set<AccountPlaylist> participations = account.getAccountPlaylists();
            for (AccountPlaylist pp :
                    participations) {
                    pp.getCollaborativePlaylist();
            }
        }else{
            throw new AccountNotFoundException(i);
        }
    }
}
