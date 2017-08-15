package communication.msg.server;

import communication.msg.Message;
/**
 * Created by sujay on 8/14/17.
 */
public class StringMessage extends Message {
    @Override
    public String toJson() {
        return null;
    }

    @Override
    public void consumeJson(String s) {

    }
}
