package com.sujayt123;

import com.sujayt123.communication.msg.Message;
import com.sujayt123.communication.MessageDecoder;
import com.sujayt123.communication.MessageEncoder;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sujayt123.communication.msg.client.*;
import com.sujayt123.communication.msg.server.*;
import com.sujayt123.service.DbService;
import scrabble.Trie;

import static util.FunctionHelper.*;

/**
 * Created by sujay on 8/14/17.
 *
 * Policy:
 * Upon each completed turn, send a push notification to each active player of the new
 * game state. A client can choose to ignore the notification if the player is participating
 * in a different game, but more likely it should update the GameListItem list view with the
 * other game's updated information. If the player is participating in the game for which
 * he gets a notification, the client should update the game view.
 *
 * Designed such that each incoming request message has exactly one response message.
 *
 */

@ServerEndpoint(value = "/connect", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class ScrabbleEndpoint {

    public static Logger logr = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static Trie trie = new Trie();

    static DbService dbService = new DbService();

    /**
     * A map from a username of a logged-in player to his/her session.
     * A session is only truly "important" once the player has logged in.
     */

    private static Map<Session, Integer> sessionToUserIdMap = Collections.synchronizedMap(new HashMap<>());


    @OnOpen
    public void onOpen(Session session)
    {
        logr.log(Level.INFO, "Opened a new session with a websocket client.");
    }

    @OnMessage
    public void onMessage(Message m, Session session) throws IOException, EncodeException {
        logr.log(Level.INFO, "Received a message from a websocket client");
        switch(m.getType())
        {
            /* Returns authorization of whether the account was created */
            case "class com.sujayt123.communication.msg.client.CreateAccountMessage":
                CreateAccountMessage cam = (CreateAccountMessage) m;
                Optional<Integer> createRetVal = dbService.createNewAccount(cam.getUsername(), cam.getPassword());
                if (createRetVal.isPresent())
                {
                    session.getBasicRemote().sendObject(new AuthorizedMessage());
                }
                else
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                }
                break;
            /* Returns a list of games for the logged in player if request was authorized and valid */
            case "class com.sujayt123.communication.msg.client.LoginMessage":
                LoginMessage login = (LoginMessage) m;
                Optional<Integer> loginRetVal = dbService.login(login.getUsername(), login.getPassword());
                if (loginRetVal.isPresent())
                {
                    /* Mark the user as authorized via the session to user map. */
                    System.out.println(sessionToUserIdMap.containsKey(session));
                    System.out.println(sessionToUserIdMap);
                    sessionToUserIdMap.put(session, loginRetVal.get());
                    session.getUserProperties().put("username", ((LoginMessage) m).getUsername());
                    session.getBasicRemote().sendObject(new GameListMessage(dbService.getGamesForPlayer(loginRetVal.get()).orElse(new GameListItem[0])));
                }
                else
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                }
                break;
            /* Returns a game for the game view if request was authorized and valid */
            case "class com.sujayt123.communication.msg.client.ChooseGameMessage":
                if (!sessionToUserIdMap.containsKey(session))
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                    return;
                }

                ChooseGameMessage chooseGameMessage = (ChooseGameMessage) m;
                Optional<GameStateItem> getGameRetVal = dbService.getGameById(sessionToUserIdMap.get(session), chooseGameMessage.getGame_id());
                if (getGameRetVal.isPresent())
                {
                    // Send information about the game to the client.
                    session.getBasicRemote().sendObject(new GameStateMessage(getGameRetVal.get()));
                }
                else
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                }
                break;

            /* Returns a game state to each logged-in participant if request is authorized and valid */
            case "class com.sujayt123.communication.msg.client.MoveMessage":
                MoveMessage moveMessage = (MoveMessage) m;
                /*
                 * Do server-side validation of incoming data.
                 * This entails a few things:
                 * 1. Ensure the current player is logged in.
                 * 2. Ensure the current player has access to the game and
                 *    is the one supposed to make the move.
                 * 3. Ensure the boardBefore -> boardAfter transition represents
                 *    a valid state change of the board.
                 */

                // Ensure current player is logged in.
                if (!sessionToUserIdMap.containsKey(session))
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                    return;
                }

                // Ensure the current player has access to the game and that it's the current player's turn
                Optional<GameStateItem> gameVal = dbService.getGameById(sessionToUserIdMap.get(session), moveMessage.getGame_id());

                if (!gameVal.isPresent() || !gameVal.get().isClientTurn())
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                    return;
                }

                List<List<Character>> boardAfterAttemptedMove =
                        forEachBoardSquareAsNestedList((i, j) ->
                                moveMessage.getBoardAfterAttemptedMove()[i][j]);

                // Score the move and update the database.
                Optional<Map<Integer, GameStateItem>> retVal = dbService
                        .updateGameState(sessionToUserIdMap.get(session), moveMessage.getGame_id(),
                                boardAfterAttemptedMove);

                if (!retVal.isPresent())
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                    return;
                }

                // Notify all active clients who are participating in the game of the update.
                Set<Map.Entry<Session, Integer>> sessionToUserEntrySet = sessionToUserIdMap.entrySet();
                for (Map.Entry<Session, Integer> e: sessionToUserEntrySet)
                {
                    if (retVal.get().containsKey(e.getValue()))
                    {
                        GameStateItem toSend = retVal.get().get(e.getValue());
                        e.getKey().getBasicRemote().sendObject(new GameStateMessage(toSend));
                    }
                }
                break;

            /* Returns an updated GameListMessage to the client if the request is authorized and valid */
            case "class com.sujayt123.communication.msg.client.CreateGameMessage":
                // Ensure current player is logged in, and has chosen a player other than him/herself to challenge.
                if (!sessionToUserIdMap.containsKey(session))
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                }
                CreateGameMessage createGameMessage = (CreateGameMessage) m;
                if (createGameMessage.getOpponentName().equals(session.getUserProperties().get("username")))
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                    return;
                }

                Optional<Integer> createGameRetVal = dbService.createNewGame(sessionToUserIdMap.get(session), createGameMessage.getOpponentName());

                if (createGameRetVal.isPresent())
                {
                    session.getBasicRemote().sendObject(new GameListMessage(dbService.getGamesForPlayer(sessionToUserIdMap.get(session)).get()));
                }
                else
                {
                    session.getBasicRemote().sendObject(new UnauthorizedMessage());
                }
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
        // Remove the client's session from the active session map.
        sessionToUserIdMap.remove(session);
    }
}
