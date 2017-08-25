package com.sujayt123.communication.msg.client;

import com.sujayt123.communication.msg.Message;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * A message relaying the credentials for a login request.
 *
 * Created by sujay on 8/14/17.
 */
public class LoginMessage extends Message {

    /**
     * The username credential for this login request.
     */
    private String username;

    /**
     * The password credential for this login request.
     */
    private String password;

    /**
     * Constructor.
     * @param username the username to set
     * @param password the password to set
     */
    public LoginMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Getter for the username.
     * @return the username for this instance
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the password.
     * @return the password for this instance
     */
    public String getPassword() {
        return password;
    }

}
