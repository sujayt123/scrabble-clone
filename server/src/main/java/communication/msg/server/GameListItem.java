package communication.msg.server;

import java.util.UUID;

/**
 * Created by sujay on 8/15/17.
 */
public class GameListItem {
    private String opponentName;
    private int game_id;

    private int clientScore;
    private int opponentScore;

    public GameListItem(String opponentName, int game_id, int clientScore, int opponentScore) {
        this.opponentName = opponentName;
        this.game_id = game_id;
        this.clientScore = clientScore;
        this.opponentScore = opponentScore;
    }

    @Override
    public String toString()
    {
        return String.format("oppName %s game_id %d clientScore %d opponentScore %d",
                opponentName, game_id, clientScore, opponentScore);
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public int getClientScore() {
        return clientScore;
    }

    public void setClientScore(int clientScore) {
        this.clientScore = clientScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }
}
