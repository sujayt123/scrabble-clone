package com.sujayt123;

import com.sujayt123.communication.msg.Message;
import com.sujayt123.communication.MessageDecoder;
import com.sujayt123.communication.MessageEncoder;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sujayt123.communication.msg.client.*;
import com.sujayt123.communication.msg.server.*;
import com.sujayt123.service.DbService;


/**
 * Created by sujay on 8/14/17.
 */

@ServerEndpoint(value = "/connect", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class ScrabbleEndpoint {

    public static Logger logr = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static DbService dbService = new DbService();

    /**
     * A map from a username of a logged-in player to his/her session.
     * A session is only truly "important" once the player has logged in.
     */

    public Map<Session, Integer> sessionToUserIdMap = Collections.synchronizedMap(new HashMap<>());

    @OnOpen
    public void onOpen(Session session)
    {
        logr.log(Level.INFO, "Opened a new session with a websocket client.");
        System.out.println("The method getClass().toString() on a stringmessage returns: " + new StringMessage("").getType());
    }

    @OnMessage
    public void onMessage(Message m, Session session) throws IOException, EncodeException {
        logr.log(Level.INFO, "Received a message from a websocket client");
        switch(m.getType())
        {
            case "class com.sujayt123.communication.msg.client.CreateAccountMessage":
                CreateAccountMessage cam = (CreateAccountMessage) m;
                if (dbService.createNewAccount(cam.getUsername(), cam.getPassword()))
                {
                    session.getBasicRemote().sendObject(new AuthorizedMessage());
                    session.getBasicRemote().sendObject(new GameListMessage(new GameListItem[0]));
                }
                else
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                }
                break;
            case "class com.sujayt123.communication.msg.client.LoginMessage":
                LoginMessage login = (LoginMessage) m;
                Optional<Integer> loginRetVal = dbService.login(login.getUsername(), login.getPassword());
                if (loginRetVal.isPresent())
                {
                    /* Mark the user as authorized via the session to user map. */
                    sessionToUserIdMap.put(session, loginRetVal.get());
                    session.getBasicRemote().sendObject(new AuthorizedMessage());
                    session.getBasicRemote().sendObject(new GameListMessage(dbService.getGamesForPlayer(loginRetVal.get()).orElse(new GameListItem[0])));
                }
                else
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                }
                break;
            case "class com.sujayt123.communication.msg.client.ChooseGameMessage":
                ChooseGameMessage chooseGameMessage = (ChooseGameMessage) m;
                Optional<GameStateItem> getGameRetVal = dbService.getGameById(sessionToUserIdMap.get(session), chooseGameMessage.getGame_id());
                if (getGameRetVal.isPresent())
                {
                    session.getBasicRemote().sendObject(new GameStateMessage(getGameRetVal.get()));
                }
                else
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                }
                break;
            case "class com.sujayt123.communication.msg.client.MoveMessage":
                //TODO
                break;

            default:
                session.getBasicRemote().sendObject(new ConfusedMessage());
                break;
        }
    }

    @OnClose
    public void onClose(Session session)
    {
        logr.log(Level.INFO, "Closing session with websocket client.");
    }
}
