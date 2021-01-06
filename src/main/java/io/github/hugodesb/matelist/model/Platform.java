package io.github.hugodesb.matelist.model;

public enum Platform {

    SPOTIFY("spotify"), DEEZER("deezer");

    private String name;

    Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
