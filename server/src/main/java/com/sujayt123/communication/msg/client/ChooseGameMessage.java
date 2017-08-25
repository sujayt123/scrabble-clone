package com.sujayt123.communication.msg.client;

import com.sujayt123.communication.msg.Message;

/**
 * A message relaying the desire to obtain information about a selected game.
 *
 * Created by sujay on 8/14/17.
 */
public class ChooseGameMessage extends Message {
    /**
     * The id of the selected game.
     */
    private int game_id;

    /**
     * Constructor.
     * @param game_id the game id to set
     */
    public ChooseGameMessage(int game_id) {
        this.game_id = game_id;
    }

    /**
     * Getter for game id.
     * @return the game id to get
     */
    public int getGame_id() {
        return game_id;
    }
}