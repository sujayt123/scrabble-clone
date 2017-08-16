package communication.msg.server;

import java.util.UUID;

/**
 * Created by sujay on 8/15/17.
 */
public class GameListItem {
    String opponentName;
    int game_id;

    int clientScore;
    int opponentScore;

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
}
