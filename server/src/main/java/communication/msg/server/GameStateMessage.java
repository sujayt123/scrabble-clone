package communication.msg.server;

import communication.msg.Message;

/**
 * Created by sujay on 8/15/17.
 */
public class GameStateMessage extends Message {
    private GameStateItem gsi;

    public GameStateItem getGsi() {
        return gsi;
    }

    public void setGsi(GameStateItem gsi) {
        this.gsi = gsi;
    }


    public GameStateMessage(GameStateItem gsi) {
        this.gsi = gsi;
    }
}
