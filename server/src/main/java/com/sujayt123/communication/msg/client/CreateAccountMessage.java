package com.sujayt123.communication.msg.client;

import com.sujayt123.communication.msg.Message;

import com.google.gson.JsonElement;
import com.google.gson.Gson;

/**
 * A message relaying the credentials for a desired new account.
 *
 * Created by sujay on 8/14/17.
 */
public class CreateAccountMessage extends Message {

    /**
     * The desired username for the account creation.
     */
    private String username;

    /**
     * The desired password for the account creation.
     */
    private String password;

    /**
     * Constructor.
     * @param username the username to set
     * @param password the password to set
     */
    public CreateAccountMessage(String username, String password) {
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
