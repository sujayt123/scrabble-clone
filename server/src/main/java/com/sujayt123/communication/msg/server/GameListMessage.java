package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;

import java.util.Arrays;

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
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for (GameListItem g: games)
            s.append(g.toString()).append('\n');
        return s.toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null || !(other instanceof GameListMessage))
        {
            return false;
        }
        GameListMessage otherGlm = (GameListMessage) other;

        return Arrays.equals(games, otherGlm.getGames());
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(games);
    }
}
