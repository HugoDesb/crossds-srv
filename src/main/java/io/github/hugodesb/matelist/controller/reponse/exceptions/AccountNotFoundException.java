package io.github.hugodesb.matelist.controller.reponse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Account")
public class AccountNotFoundException extends RuntimeException{

    private Long id;

    public AccountNotFoundException(Long id) {
        super("Account with id="+id+" not found");
        this.id = id;
    }
}
