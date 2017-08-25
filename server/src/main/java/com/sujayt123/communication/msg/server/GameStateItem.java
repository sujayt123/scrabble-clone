package com.sujayt123.communication.msg.server;

import java.util.Arrays;

/**
 * A more detailed description of a client's vision of a particular game.
 *
 * Created by sujay on 8/15/17.
 */
public class GameStateItem extends GameListItem {

    /**
     * The last state the game board was in.
     */
    private char[][] oldBoard;

    /**
     * The current game board.
     */
    private char[][] board;

    /**
     * A string containing all the characters corresponding to the client's rack ("hand").
     */
    private String clientHand;

    /**
     * Indicator of whether it's the client's turn to make a move.
     */
    private boolean clientTurn;

    /**
     * Constructor.
     * @param opponentName the opponent name to set
     * @param game_id the game id to set
     * @param clientScore the client score to set
     * @param opponentScore the opponent score to set
     * @param board the board to set
     * @param oldBoard the old board to set
     * @param clientHand the client hand to set
     * @param clientTurn the client turn to set
     */
    public GameStateItem(String opponentName, int game_id, int clientScore, int opponentScore, char[][] board, char[][] oldBoard, String clientHand, boolean clientTurn) {
        super(opponentName, game_id, clientScore, opponentScore);
        this.board = board;
        this.clientHand = clientHand;
        this.oldBoard = oldBoard;
        this.clientTurn = clientTurn;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append(super.toString()).append('\n');
        s.append("old board:\n").append(Arrays.deepToString(oldBoard));
        s.append("\n\n");
        s.append("current board:\n").append(Arrays.deepToString(board));
        s.append("\n\n");
        s.append("hand\n").append(clientHand).append('\n');
        s.append("clientTurn:\n").append(clientTurn);
        return s.toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if(!super.equals(other))
            return false;

        if(!(other instanceof GameStateItem))
            return false;

        GameStateItem otherItem = (GameStateItem) other;

        if (this.clientHand == null ?
                otherItem.getClientHand() != null : !clientHand.equals(otherItem.getClientHand()))
            return false;

        return Arrays.deepEquals(oldBoard, otherItem.getOldBoard()) &&
                Arrays.deepEquals(board, otherItem.getBoard()) &&
                this.clientTurn == otherItem.isClientTurn();
    }

    @Override
    public int hashCode()
    {
        int hash = super.hashCode();
        hash += Arrays.deepHashCode(board) + Arrays.deepHashCode(oldBoard);
        hash += clientHand == null ? 0 : clientHand.hashCode();
        hash += clientTurn ? 1 : 0;
        return hash;
    }

    /**
     * Getter for the current state of the board.
     * @return the current state of the board for this game.
     */
    public char[][] getBoard() {
        return board;
    }

    /**
     * Getter for the client hand.
     * @return the client's hand (rack)
     */
    public String getClientHand() {
        return clientHand;
    }

    /**
     * Getter for the client turn.
     * @return whether it is the client's turn to make a move
     */
    public boolean isClientTurn() {
        return clientTurn;
    }

    /**
     * Getter for the last state of the board (before the last move).
     * @return the state of the board prior to the last move
     */
    public char[][] getOldBoard() {
        return oldBoard;
    }

}
