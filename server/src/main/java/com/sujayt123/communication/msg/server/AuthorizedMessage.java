package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;

/**
 * A message indicating that an account creation was authorized.
 *
 * Created by sujay on 8/17/17.
 */
public class AuthorizedMessage extends Message {
    @Override
    public boolean equals(Object other)
    {
        return (other != null && other instanceof AuthorizedMessage);
    }

    @Override
    public int hashCode()
    {
        return 111;
    }
}
