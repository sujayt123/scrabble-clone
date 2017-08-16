package communication.msg.client;

import communication.msg.Message;

import java.util.UUID;

/**
 * Created by sujay on 8/14/17.
 */
public class ChooseGameMessage extends Message {
    private int game_id;

    public ChooseGameMessage(int game_id) {
        this.game_id = game_id;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }
}
