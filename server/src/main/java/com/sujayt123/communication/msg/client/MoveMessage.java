package com.sujayt123.communication.msg.client;

import com.sujayt123.communication.msg.Message;

/**
 * Created by sujay on 8/14/17.
 */
public class MoveMessage extends Message {

    private int game_id;
    private char[][] boardAfterAttemptedMove;

    public MoveMessage(int game_id, char[][] boardAfterAttemptedMove) {
        this.game_id = game_id;
        this.boardAfterAttemptedMove = boardAfterAttemptedMove;
    }

    public char[][] getBoardAfterAttemptedMove() {
        return boardAfterAttemptedMove;
    }

    public void setBoardAfterAttemptedMove(char[][] boardAfterAttemptedMove) {
        this.boardAfterAttemptedMove = boardAfterAttemptedMove;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }
}
