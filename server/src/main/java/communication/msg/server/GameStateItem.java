package communication.msg.server;

import java.util.Arrays;

/**
 * Created by sujay on 8/15/17.
 */
public class GameStateItem extends GameListItem {

    private char[][] board;

    private char[] clientHand;

    public GameStateItem(String opponentName, int game_id, int clientScore, int opponentScore, char[][] board, char[] clientHand) {
        super(opponentName, game_id, clientScore, opponentScore);
        this.board = board;
        this.clientHand = clientHand;
    }

    @Override
    public String toString()
    {
        String out = super.toString() + "\nboard:\n";
        for (int i = 0 ; i < 15; i++)
        {
            out += Arrays.toString(board[i]);
        }
        out += "\nhand\n" + Arrays.toString(clientHand);
        return out;
    }

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public char[] getClientHand() {
        return clientHand;
    }

    public void setClientHand(char[] clientHand) {
        this.clientHand = clientHand;
    }
}
