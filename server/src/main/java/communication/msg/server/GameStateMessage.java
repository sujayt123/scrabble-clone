package communication.msg.server;

import communication.msg.Message;

/**
 * Created by sujay on 8/15/17.
 */
public class GameStateMessage extends Message {
    int clientScore;
    int opponentScore;
    String[][] board;

    String mostRecentlyPlayedWord;
    boolean playedByClient;
}
