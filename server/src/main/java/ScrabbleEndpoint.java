import communication.msg.Message;
import communication.MessageDecoder;
import communication.MessageEncoder;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sujay on 8/14/17.
 */

@ServerEndpoint(value = "/connect", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class ScrabbleEndpoint {

    /**
     * A map from a username of a logged-in player to his/her session.
     *
     * A session is only truly "important" once the player has logged in.
     */

    public Map<String, Session> userToSessionMap = Collections.synchronizedMap(new HashMap<>());

    @OnOpen
    public void onOpen(Session session)
    {

    }

    @OnMessage
    public void onMessage(Message m, Session session)
    {

    }

    @OnClose
    public void onClose(Session session)
    {

    }
}
