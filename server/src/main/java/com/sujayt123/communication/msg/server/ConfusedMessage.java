package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;

/**
 * A message indicating confusion on behalf of the server's inability to understand a request.
 *
 * Created by sujay on 8/17/17.
 */
public class ConfusedMessage extends Message {
    @Override
    public boolean equals(Object other)
    {
        return (other != null && other instanceof ConfusedMessage);
    }

    @Override
    public int hashCode()
    {
        return 1;
    }
}
