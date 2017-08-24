package com.sujayt123.communication.msg.client;

import com.sujayt123.communication.msg.Message;

/**
 * Created by sujay on 8/20/17.
 */
public class CreateGameMessage extends Message {

    private String opponentName;

    public CreateGameMessage(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

}
