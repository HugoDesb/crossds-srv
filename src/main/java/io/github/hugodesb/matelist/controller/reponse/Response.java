package io.github.hugodesb.matelist.controller.reponse;


public abstract class Response<D, E> {
    private boolean success;
    private D data;
    private E error;
}
