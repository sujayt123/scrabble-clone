package com.sujayt123;

import com.sujayt123.communication.msg.Message;
import com.sujayt123.communication.MessageDecoder;
import com.sujayt123.communication.MessageEncoder;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sujay on 8/14/17.
 */

@ServerEndpoint(value = "/connect", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class ScrabbleEndpoint {

    private Logger logr = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * A map from a username of a logged-in player to his/her session.
     * A session is only truly "important" once the player has logged in.
     */

    public Map<Session, Integer> sessionToUserIdMap = Collections.synchronizedMap(new HashMap<>());

    @OnOpen
    public void onOpen(Session session)
    {
        logr.log(Level.INFO, "Opened a new session with a websocket client.");
    }

    @OnMessage
    public void onMessage(Message m, Session session)
    {
        logr.log(Level.INFO, "Received a message from a websocket client");
    }

    @OnClose
    public void onClose(Session session)
    {
        logr.log(Level.INFO, "Closing session with websocket client.");
    }
}
