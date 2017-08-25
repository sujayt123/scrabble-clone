package com.sujayt123.communication.msg.server;

/**
 * Information about a single game for a "selection" view in the client.
 *
 * Created by sujay on 8/15/17.
 */
public class GameListItem {

    /**
     * The name of the opposing player in this game.
     */
    private String opponentName;

    /**
     * The unique id of the game.
     */
    private int game_id;

    /**
     * The client's score in this game
     */
    private int clientScore;

    /**
     * The opponent's score in this game
     */
    private int opponentScore;

    /**
     * Constructor.
     *
     * @param opponentName  the opponent name to set
     * @param game_id       the game id to set
     * @param clientScore   the client score to set
     * @param opponentScore the opponent score to set
     */
    public GameListItem(String opponentName, int game_id, int clientScore, int opponentScore) {
        this.opponentName = opponentName;
        this.game_id = game_id;
        this.clientScore = clientScore;
        this.opponentScore = opponentScore;
    }

    @Override
    public String toString() {
        return String.format("oppName=%s game_id=%d clientScore=%d opponentScore=%d",
                opponentName, game_id, clientScore, opponentScore);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof GameListItem)) {
            return false;
        }

        GameListItem otherItem = (GameListItem) other;
        return this.game_id == otherItem.getGame_id()
                && (opponentName == null) ?
                otherItem.opponentName == null :
                this.opponentName.equals(otherItem.getOpponentName())
                        && this.clientScore == otherItem.getClientScore()
                        && this.opponentScore == otherItem.getOpponentScore();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += game_id * 7;
        hash += clientScore * 11 + opponentScore * 131;
        hash += (opponentName == null) ? 0 : opponentName.hashCode();
        return hash;
    }

    /**
     * Getter for the opponent name.
     * @return the opponent's name
     */
    public String getOpponentName() {
        return opponentName;
    }

    /**
     * Getter for the game id.
     * @return the game id
     */
    public int getGame_id() {
        return game_id;
    }

    /**
     * Getter for the client's score.
     * @return the client's score.
     */
    public int getClientScore() {
        return clientScore;
    }

    /**
     * Getter for the opponent's score.
     * @return the opponent's score
     */
    public int getOpponentScore() {
        return opponentScore;
    }

}