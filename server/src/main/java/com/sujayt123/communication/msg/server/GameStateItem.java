package com.sujayt123.communication.msg.server;

import java.util.Arrays;

/**
 * Created by sujay on 8/15/17.
 */
public class GameStateItem extends GameListItem {

    private char[][] board;

    private String clientHand;

    private String mostRecentWord;

    private boolean clientTurn;

    public GameStateItem(String opponentName, int game_id, int clientScore, int opponentScore, char[][] board, String clientHand, String mostRecentWord, boolean p1turn) {
        super(opponentName, game_id, clientScore, opponentScore);
        this.board = board;
        this.clientHand = clientHand;
        this.mostRecentWord = mostRecentWord;
        this.clientTurn = p1turn;
    }

    @Override
    public String toString()
    {
        String out = super.toString() + "\nboard:\n";
        for (int i = 0 ; i < 15; i++)
        {
            out += Arrays.toString(board[i]);
        }
        out += "\nhand\n" + clientHand;
        out += "\nmostRecentWord" + ((mostRecentWord == null) ? "(none)" : mostRecentWord);
        out +="\n clientTurn: " + clientTurn;
        return out;
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


    public String getMostRecentWord() {
        return mostRecentWord;
    }

    public void setMostRecentWord(String mostRecentWord) {
        this.mostRecentWord = mostRecentWord;
    }

    public boolean isClientTurn() {
        return clientTurn;
    }

    public void setClientTurn(boolean clientTurn) {
        this.clientTurn = clientTurn;
    }

}
