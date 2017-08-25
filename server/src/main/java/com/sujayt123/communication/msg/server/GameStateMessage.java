package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;

/**
 * Created by sujay on 8/15/17.
 */
public class GameStateMessage extends Message {
    private GameStateItem gsi;

    public GameStateItem getGsi() {
        return gsi;
    }

    public void setGsi(GameStateItem gsi) {
        this.gsi = gsi;
    }

    public GameStateMessage(GameStateItem gsi) {
        this.gsi = gsi;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null || !(other instanceof GameStateMessage))
            return false;
        return (gsi == null)
                ? ((GameStateMessage) other).getGsi() == null : gsi.equals(((GameStateMessage) other).getGsi());
    }

    @Override
    public int hashCode()
    {
        return (gsi == null) ? 7 : gsi.hashCode();
    }
}
