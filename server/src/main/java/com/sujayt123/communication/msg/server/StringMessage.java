package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Created by sujay on 8/14/17.
 */
public class StringMessage extends Message {

    private String message;

    public StringMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null || !(other instanceof StringMessage))
        {
            return false;
        }
        return this.message.equals(((StringMessage) other).getMessage());
    }
}
