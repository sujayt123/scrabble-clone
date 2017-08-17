package com.sujayt123.communication.msg.client;

import com.sujayt123.communication.msg.Message;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Created by sujay on 8/14/17.
 */
public class LoginMessage extends Message {

    private String username;
    private String password;

    public LoginMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}