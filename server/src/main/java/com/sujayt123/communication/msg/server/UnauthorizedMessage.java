package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;

/**
 * A message to indicate that a client request was invalid or unauthorized.
 *
 * Created by sujay on 8/15/17.
 */
public class UnauthorizedMessage extends Message {
    @Override
    public boolean equals(Object other)
    {
        return (other != null && other instanceof UnauthorizedMessage);
    }

    @Override
    public int hashCode()
    {
        return 77;
    }
}
