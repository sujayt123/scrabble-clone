package com.sujayt123.communication.msg.client;

import com.sujayt123.communication.msg.Message;

/**
 * A message relaying the desire to create a new game against another player.
 *
 * Created by sujay on 8/20/17.
 */
public class CreateGameMessage extends Message {

    /**
     * The name of the player against whom the client requests a new game.
     */
    private String opponentName;

    /**
     * Constructor.
     * @param opponentName the opponent name to set
     */
    public CreateGameMessage(String opponentName) {
        this.opponentName = opponentName;
    }

    /**
     * Getter for the opponent name.
     * @return the opponent name for this instance
     */
    public String getOpponentName() {
        return opponentName;
    }

}
