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
}
