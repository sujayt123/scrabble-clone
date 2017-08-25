package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;

/**
 * A message containing information regarding the client's vision of a particular game.
 *
 * Created by sujay on 8/15/17.
 */
public class GameStateMessage extends Message {

    /**
     * Detailed information about the client's view of the game.
     */
    private GameStateItem gsi;

    /**
     * Getter for the client's view of the game.
     * @return the client's view of the game
     */
    public GameStateItem getGsi() {
        return gsi;
    }


    /**
     * Constructor
     * @param gsi the client's view of the game to set
     */
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
