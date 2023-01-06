package controller.jwt;

import java.io.Serializable;

public class jwtresponsemodel implements Serializable {

    private final String token;

    public jwtresponsemodel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
