package com.sujayt123.communication.msg.server;

import java.util.Arrays;

/**
 * Created by sujay on 8/15/17.
 */
public class GameStateItem extends GameListItem {

    private char[][] oldBoard;

    private char[][] board;

    private String clientHand;

    private boolean clientTurn;

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

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public String getClientHand() {
        return clientHand;
    }

    public void setClientHand(String clientHand) {
        this.clientHand = clientHand;
    }

    public boolean isClientTurn() {
        return clientTurn;
    }

    public void setClientTurn(boolean clientTurn) {
        this.clientTurn = clientTurn;
    }


    public char[][] getOldBoard() {
        return oldBoard;
    }

    public void setOldBoard(char[][] oldBoard) {
        this.oldBoard = oldBoard;
    }

}
