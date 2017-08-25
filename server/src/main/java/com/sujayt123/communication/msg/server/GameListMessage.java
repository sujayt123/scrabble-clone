package com.sujayt123.communication.msg.server;

import com.sujayt123.communication.msg.Message;

import java.util.Arrays;

/**
 * A message containing minimal details about each game in which the client is involved.
 *
 * Created by sujay on 8/14/17.
 */
public class GameListMessage extends Message {
    /**
     * The array of game information.
     */
    private GameListItem[] games;

    /**
     * Constructor.
     * @param games the array of game information
     */
    public GameListMessage(GameListItem[] games) {
        this.games = games;
    }

    /**
     * Getter for the array of game information.
     * @return the array of game information
     */
    public GameListItem[] getGames() {
        return games;
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
