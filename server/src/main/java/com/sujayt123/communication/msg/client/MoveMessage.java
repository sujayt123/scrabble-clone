package com.sujayt123.communication.msg.client;

import com.sujayt123.communication.msg.Message;

/**
 * A message relaying a client attempt to update the board state
 * for the indicated game.
 *
 * Created by sujay on 8/14/17.
 */
public class MoveMessage extends Message {

    /**
     * The id of the game which the client has attempted to update.
     */
    private int game_id;

    /**
     * The board state of the game after the client has attempted a move.
     */
    private char[][] boardAfterAttemptedMove;

    /**
     * Constructor
     * @param game_id the game id to set
     * @param boardAfterAttemptedMove the board to set
     */
    public MoveMessage(int game_id, char[][] boardAfterAttemptedMove) {
        this.game_id = game_id;
        this.boardAfterAttemptedMove = boardAfterAttemptedMove;
    }

    /**
     * Getter for the board after the attempted move
     * @return the board after the attempted move
     */
    public char[][] getBoardAfterAttemptedMove() {
        return boardAfterAttemptedMove;
    }

    /**
     * Getter for the game id
     * @return the game id for the attempted update
     */
    public int getGame_id() {
        return game_id;
    }

}
