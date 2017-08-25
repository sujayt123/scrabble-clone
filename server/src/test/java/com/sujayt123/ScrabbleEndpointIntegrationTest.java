package com.sujayt123;

import com.sujayt123.communication.msg.Message;
import com.sujayt123.communication.msg.client.*;
import com.sujayt123.communication.msg.server.*;
import org.glassfish.tyrus.client.ClientManager;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Must be executed AFTER an instance of the server is deployed through Tomcat on port 8080.
 *
 * Note:
 * <quote> There is no more than one thread allocated to process data per client.
 * If a client sends multiple messages then they will be processed sequentially - possibly by the same thread.
 * When the thread has finished the first message it will look to see of there is more data to read.
 * If there is, it will read it. If not, the socket goes back to the Selector / Poller until more data arrives.
 * Data has to be processed this way. Where there is scope for more threads is once a message (or partial message)
 * is ready to be passed to the application, that could be done in a new thread (but isn't).</quote>
 *
 * Also:
 * <quote> If multiple messages are received from multiple clients
 * then multiple threads will be dispatched to handle those messages. </quote>
 * (https://stackoverflow.com/questions/19211754/tomcat-8-jsr-356-websocket-threading)
 *
 * Created by sujay on 8/23/17.
 */

public class ScrabbleEndpointIntegrationTest {

    /**
     * The queue where incoming messages will be stored. Must use a synchronization mechanism to ensure that
     * requests by different clients are not processed concurrently by server threads.
     * <p>
     * The producer is the container executing the client endpoint.
     * <p>
     * The consumer is the sendMessage method, which only sends a message
     * once a response has been received (as appropriate).
     */
    static final BlockingQueue<Message> msgQueue = new ArrayBlockingQueue<>(10);

    /**
     * The queue where the expected responses are stored.
     */
    private Queue<Message> expectedQueue;

    /**
     * The game id of the first created game in this test instance.
     */
    private int firstGameIdInTest;

    /**
     *  Pops an item off the blocking queue and checks to see if it equals the
     *  expected response on the expected queue.
     */
    private Function<BlockingQueue<Message>, Boolean> equalsAssertion;

    /**
     * Pops three items off the blocking queue corresponding to the received responses
     * to a valid update to the board state (via a player's turn). Three items because
     * in this integration test, three separate clients are participants of the game
     * (under the identity of 2 players).
     */
    private Function<BlockingQueue<Message>, Boolean> moveVerifyingAssertion;

    @Before
    public void setup() {
        GameListItem[] emptyGameListSingleton = new GameStateItem[0];
        equalsAssertion =
            blockingQueue -> {
                try {
                    Message received = blockingQueue.take();
                    Message expected = expectedQueue.poll();
                    return expected.equals(received);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
            };
        moveVerifyingAssertion =
                (blockingQueue) -> {
                    try {
                        Message m1, m2, m3;
                        if ((m1 = blockingQueue.take()) instanceof UnauthorizedMessage)
                            return false;
                        if ((m2 = blockingQueue.take()) instanceof UnauthorizedMessage)
                            return false;
                        if ((m3 = blockingQueue.take()) instanceof UnauthorizedMessage)
                            return false;
                        if (!(m1 instanceof GameStateMessage
                                && m2 instanceof GameStateMessage
                                && m3 instanceof GameStateMessage))
                            return false;
                        Set<GameStateItem> receivedSet = new HashSet<>();
                        receivedSet.add(((GameStateMessage) m1).getGsi());
                        receivedSet.add(((GameStateMessage) m2).getGsi());
                        receivedSet.add(((GameStateMessage) m3).getGsi());
                        Set<GameStateItem> expectedSet = new HashSet<>();
                        expectedSet.add(((GameStateMessage)expectedQueue.poll()).getGsi());
                        expectedSet.add(((GameStateMessage)expectedQueue.poll()).getGsi());
                        expectedSet.add(((GameStateMessage)expectedQueue.poll()).getGsi());
                        return (expectedSet.equals(receivedSet));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return false;
                };
        expectedQueue = new ConcurrentLinkedDeque<>();
        firstGameIdInTest = ScrabbleEndpoint.dbService.getIdOfNextGame().get();
        expectedQueue.add(new AuthorizedMessage());
        expectedQueue.add(new UnauthorizedMessage());
        expectedQueue.add(new AuthorizedMessage());
        expectedQueue.add(new UnauthorizedMessage());
        expectedQueue.add(new GameListMessage(emptyGameListSingleton));
        expectedQueue.add(new UnauthorizedMessage());
        expectedQueue.add(new GameListMessage(emptyGameListSingleton));
        expectedQueue.add(new GameListMessage(emptyGameListSingleton));
        expectedQueue.add(new UnauthorizedMessage());
        expectedQueue.add(new GameListMessage(
                new GameListItem[]{
                        new GameListItem("user1", firstGameIdInTest, 0, 0)
                }));
        expectedQueue.add(new GameListMessage(
                new GameListItem[]{
                        new GameListItem("user2", firstGameIdInTest, 0, 0),
                        new GameListItem("user2", firstGameIdInTest + 1, 0, 0)
                }));
        expectedQueue.add(new GameListMessage(
                new GameListItem[]{
                        new GameListItem("user1", firstGameIdInTest, 0, 0),
                        new GameListItem("user1", firstGameIdInTest + 1, 0, 0)
                }));
        expectedQueue.add(new GameListMessage(
                new GameListItem[]{
                        new GameListItem("user2", firstGameIdInTest, 0, 0),
                        new GameListItem("user2", firstGameIdInTest + 1, 0, 0)
                }));
        expectedQueue.add(new GameListMessage(
                new GameListItem[]{
                        new GameListItem("user2", firstGameIdInTest, 0, 0),
                        new GameListItem("user2", firstGameIdInTest + 1, 0, 0)
                }));

        expectedQueue.add(new GameStateMessage(new GameStateItem(
                "user1",
                firstGameIdInTest,
                0,
                0,
                generateEmptyBoard(),
                generateEmptyBoard(),
                "ETSWOID",
                true
        )));

        expectedQueue.add(new GameStateMessage(new GameStateItem(
                "user2",
                firstGameIdInTest,
                0,
                0,
                generateEmptyBoard(),
                generateEmptyBoard(),
                "AGISEGO",
                false
        )));

        expectedQueue.add(new UnauthorizedMessage());
        expectedQueue.add(new UnauthorizedMessage());

        /*
         * After the first game update, user2 receives a GameStateMessage and each of user1's 2 game
         *  clients receive a GameStateMessage. Recall that user2 created this game.
         */
        char[][] boardAfterFirstMove = generateEmptyBoard();
        boardAfterFirstMove[7][7] = 'S';
        boardAfterFirstMove[7][8] = 'E';
        boardAfterFirstMove[7][9] = 'T';

        expectedQueue.add(new GameStateMessage(new GameStateItem(
                "user1",
                firstGameIdInTest,
                6,
                0,
                boardAfterFirstMove,
                generateEmptyBoard(),
                "WOIDIYI",
                false
        )));

        expectedQueue.add(new GameStateMessage(new GameStateItem(
                "user2",
                firstGameIdInTest,
                0,
                6,
                boardAfterFirstMove,
                generateEmptyBoard(),
                "AGISEGO",
                true
        )));

        expectedQueue.add(new GameStateMessage(new GameStateItem(
                "user2",
                firstGameIdInTest,
                0,
                6,
                boardAfterFirstMove,
                generateEmptyBoard(),
                "AGISEGO",
                true
        )));

        expectedQueue.add(new UnauthorizedMessage());
        expectedQueue.add(new UnauthorizedMessage());

        char[][] boardAfterSecondMove = generateEmptyBoard();

        boardAfterSecondMove[7][7] = 'S';
        boardAfterSecondMove[7][8] = 'E';
        boardAfterSecondMove[7][9] = 'T';
        boardAfterSecondMove[6][8] = 'A';

        expectedQueue.add(new GameStateMessage(new GameStateItem(
                "user1",
                firstGameIdInTest,
                6,
                3,
                boardAfterSecondMove,
                boardAfterFirstMove,
                "WOIDIYI",
                true
        )));

        expectedQueue.add(new GameStateMessage(new GameStateItem(
                "user2",
                firstGameIdInTest,
                3,
                6,
                boardAfterSecondMove,
                boardAfterFirstMove,
                "GISEGOH",
                false
        )));

        expectedQueue.add(new GameStateMessage(new GameStateItem(
                "user2",
                firstGameIdInTest,
                3,
                6,
                boardAfterSecondMove,
                boardAfterFirstMove,
                "GISEGOH",
                false
        )));

    }

    private static char[][] generateEmptyBoard()
    {
        return new char[][] {
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
        };
    }

    @Test
    public void testEndpoint() throws URISyntaxException, IOException, DeploymentException, EncodeException, InterruptedException {

        /* Use privileged services to remove the testing accounts */
        ScrabbleEndpoint.dbService.deleteExistingAccount("user1");
        ScrabbleEndpoint.dbService.deleteExistingAccount("user2");

        ClientManager client1 = ClientManager.createClient();
        ClientManager client2 = ClientManager.createClient();
        ClientManager client3 = ClientManager.createClient();

        Session s1 = client1.connectToServer(SampleClientEndpoint.class, new URI("ws://localhost:8080/connect"));
        Session s2 = client2.connectToServer(SampleClientEndpoint.class, new URI("ws://localhost:8080/connect"));
        Session s3 = client3.connectToServer(SampleClientEndpoint.class, new URI("ws://localhost:8080/connect"));

        /* Creating an account automatically logs you in. */

        sendMessage(s1, new CreateAccountMessage("user1", "password123")); // Should return AuthorizedMessage
        sendMessage(s2, new CreateAccountMessage("user1", "doesn'tmatter")); // Should return UnAuthorizedMessage
        sendMessage(s3, new CreateAccountMessage("user2", "differentCombination!1")); // Should return AuthorizedMessage
        /* At this stage, no one is logged in. */

        /* Logging in updates your session with the most recently validated account access. */

        sendMessage(s1, new LoginMessage("user2", "notTheRightCombination1!")); // Should return UnAuthorized Message
        sendMessage(s1, new LoginMessage("user2", "differentCombination!1")); // Should return GameListMessage
        sendMessage(s2, new LoginMessage("user1", "doesn'tmatter")); // Should return unauthorized message
        sendMessage(s2, new LoginMessage("user1", "password123")); // Should return GameListMessage
        sendMessage(s3, new LoginMessage("user1", "password123")); // Should return GameListMessage
        /* At this stage, s1 is logged in as user2, and s2 and s3 are logged in as user1. */

        sendMessage(s1, new CreateGameMessage("user2")); // Should return UnauthorizedMessage
        sendMessage(s1, new CreateGameMessage("user1")); // Should return GameListMessage
        sendMessage(s2, new CreateGameMessage("user2")); // Should return GameListMessage

        /* Simple sanity checks on user1 and user2's game lists so far */
        // TODO change so that user can get gamelist without having to log in again
        sendMessage(s1, new LoginMessage("user2", "differentCombination!1")); //Should get gameList for user2
        sendMessage(s2, new LoginMessage("user1", "password123")); // Should get gamelist for user1
        sendMessage(s3, new LoginMessage("user1", "password123")); // Should get gamelist for user1

        /* Do user1 and user2 get correct visions of the board / their hands + turns */
        sendMessage(s1, new ChooseGameMessage(firstGameIdInTest));
        sendMessage(s2, new ChooseGameMessage(firstGameIdInTest));

        /* User2 tries to make a gameupdate move */
        char[][] invalidBoard = generateEmptyBoard();
        invalidBoard[0][0] = 'S';
        invalidBoard[1][0] = 'E';
        invalidBoard[2][0] = 'W';
        sendMessage(s1, new MoveMessage(firstGameIdInTest, invalidBoard)); // Should yield UnauthorizedMessage

        invalidBoard[0][0] = ' ';
        invalidBoard[1][0] = ' ';
        invalidBoard[2][0] = ' ';
        invalidBoard[7][7] = 'S';
        invalidBoard[7][8] = 'E';
        sendMessage(s1, new MoveMessage(firstGameIdInTest, invalidBoard)); // Should yield UnauthorizedMessage

        char[][] validBoard = generateEmptyBoard();
        validBoard[7][7] = 'S';
        validBoard[7][8] = 'E';
        validBoard[7][9] = 'T';

        sendMessage(s1, new MoveMessage(firstGameIdInTest, validBoard), moveVerifyingAssertion); // should return three messages of gamestates

        /*
         * User2 tries again to make a game update move, but fails. Even though she
         * uses her own tiles and places them correctly, it's not her turn!
         */
        char[][] validButNotRightTurn = generateEmptyBoard();
        validButNotRightTurn[7][7] = 'S';
        validButNotRightTurn[7][8] = 'E';
        validButNotRightTurn[7][9] = 'T';
        validButNotRightTurn[6][8] = 'W';
        sendMessage(s1, new MoveMessage(firstGameIdInTest, validButNotRightTurn)); // should return unauthorized message

        /*
         * User1 makes a move in response to User2's first move.
         */
        char[][] useTilesNotFromHand = validButNotRightTurn;
        sendMessage(s2, new MoveMessage(firstGameIdInTest, useTilesNotFromHand)); // should return unauthorized message

        validBoard = generateEmptyBoard();
        validBoard[7][7] = 'S';
        validBoard[7][8] = 'E';
        validBoard[7][9] = 'T';
        validBoard[6][8] = 'A';

        sendMessage(s2, new MoveMessage(firstGameIdInTest, validBoard), moveVerifyingAssertion); // should return three messages of gamestates

        assertEquals(0, expectedQueue.size());
        assertEquals(0, msgQueue.size());

        ScrabbleEndpoint.dbService.deleteExistingAccount("user1");
        ScrabbleEndpoint.dbService.deleteExistingAccount("user2");

        s1.close();
        s2.close();
        s3.close();
    }

    /**
     *
     * @param s
     * @param m
     * @param
     * @throws IOException
     * @throws EncodeException
     * @throws InterruptedException
     */
    private void sendMessage(Session s, Message m, Function<BlockingQueue<Message>, Boolean> assertionToApply) throws IOException, EncodeException, InterruptedException {
        /* Send the message */
        s.getBasicRemote().sendObject(m);

        /* Apply the assertion to the response, blocking as needed */
        if (!assertionToApply.apply(msgQueue)) {
                assertFalse(true);
        }
    }

    /**
     *
     * @param s
     * @param m
     * @throws IOException
     * @throws EncodeException
     * @throws InterruptedException
     */

    private void sendMessage(Session s, Message m) throws IOException, EncodeException, InterruptedException {
        sendMessage(s, m, equalsAssertion);
    }
}