package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;

/**
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
