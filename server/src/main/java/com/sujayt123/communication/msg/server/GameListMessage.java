package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;

/**
 * Created by sujay on 8/14/17.
 */
public class GameListMessage extends Message {
    public GameListMessage(GameListItem[] games) {
        this.games = games;
    }

    private GameListItem[] games;

    public GameListItem[] getGames() {
        return games;
    }

    public void setGames(GameListItem[] games) {
        this.games = games;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null || !(other instanceof GameListMessage))
        {
            return false;
        }
        GameListMessage otherGlm = (GameListMessage) other;

        if (this.games.length != otherGlm.getGames().length)
            return false;

        for (int i = 0; i < this.games.length; i++)
        {
            if (this.games[i] == null || !this.games[i].equals(otherGlm.getGames()[i]))
                return false;
        }
        return true;
    }
}
