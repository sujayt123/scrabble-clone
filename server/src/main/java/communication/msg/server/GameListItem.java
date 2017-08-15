package communication.msg.server;

import java.util.UUID;

/**
 * Created by sujay on 8/15/17.
 */
public class GameListItem {
    String opponentName;
    String gameName;
    UUID gameUuid;

    int clientScore;
    int opponentScore;
}
