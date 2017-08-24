package com.sujayt123;

import com.sujayt123.communication.msg.Message;
import com.sujayt123.communication.msg.client.CreateAccountMessage;
import com.sujayt123.communication.msg.client.CreateGameMessage;
import com.sujayt123.communication.msg.client.LoginMessage;
import com.sujayt123.communication.msg.server.GameListItem;
import com.sujayt123.communication.msg.server.GameListMessage;
import com.sujayt123.communication.msg.server.GameStateItem;
import com.sujayt123.communication.msg.server.UnauthorizedMessage;
import com.sujayt123.service.DbService;
import org.glassfish.tyrus.client.ClientManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;

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

    /** The queue where incoming messages will be stored. Must use a synchronization mechanism to ensure that
     *  requests by different clients are not processed concurrently by server threads.
     *
     *  The producer is the container executing the client endpoint.
     *
     *  The consumer is the sendMessage method, which only sends a message
     *  once a response has been received (as appropriate).
     *
     */
    static final BlockingQueue<Message> msgQueue = new ArrayBlockingQueue<>(10);

    private Queue<Message> expectedQueue;

    private boolean requestMade;

    @Before
    public void setup()
    {
        GameListItem[] emptyGameListSingleton = new GameStateItem[0];
        expectedQueue = new ConcurrentLinkedDeque<>();
        requestMade = false;
        expectedQueue.add(new GameListMessage(emptyGameListSingleton));
        expectedQueue.add(new UnauthorizedMessage());
        expectedQueue.add(new GameListMessage(emptyGameListSingleton));
        expectedQueue.add(new UnauthorizedMessage());
        expectedQueue.add(new GameListMessage(emptyGameListSingleton));
        expectedQueue.add(new UnauthorizedMessage());
        expectedQueue.add(new GameListMessage(emptyGameListSingleton));
        expectedQueue.add(new GameListMessage(emptyGameListSingleton));
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

        sendMessage(s1, new CreateAccountMessage("user1", "password123")); // Should return GameListMessage
        sendMessage(s2 , new CreateAccountMessage("user1", "doesn'tmatter")); // Should return UnAuthorizedMessage
        sendMessage(s3, new CreateAccountMessage("user2", "differentCombination!1")); // Should return GameListMessage
        /* At this stage, s1 and s3 are both logged in as user1 and user2 respectively. s2 is not logged in. */

        /* Logging in updates your session with the most recently validated account access. */

        sendMessage(s1, new LoginMessage("user2", "notTheRightCombination1!")); // Should return UnAuthorized Message
        sendMessage(s1, new LoginMessage("user2", "differentCombination!1")); // Should return GameListMessage
        sendMessage(s2, new LoginMessage("user1", "doesn'tmatter")); // Should return unauthorized message
        sendMessage(s2, new LoginMessage("user1", "password123")); // Should return GameListMessage
        sendMessage(s3, new LoginMessage("user1", "password123")); // Should return GameListMessage
        /* At this stage, s1 is logged in as user2, and s2 and s3 are logged in as user1. */

        /* Now create a new game as user1 against user2. We should get 3 messages:
         * - 1 for user2's vision of the game
         * - 2 for user1's vision of the game
         *
         * These may be added to the queue in any order. We can't predict the game contents as we're testing
         * against the actual server rather than a mock, so just ensure that contents are equivalent as needed.
         *
         * TODO
         */

//        sendMessage(s1, new CreateGameMessage("user2"));
//
//        Thread.sleep(1500);
//
//        assertEquals(3, msgQueue.size());
//        Message msg1 = msgQueue.take();
//        Message msg2 = msgQueue.take();
//        Message msg3 = msgQueue.take();
//        assertTrue(msg1.equals(msg2) || msg2.equals(msg3) || msg1.equals(msg3));

        ScrabbleEndpoint.dbService.deleteExistingAccount("user1");
        ScrabbleEndpoint.dbService.deleteExistingAccount("user2");

        s1.close();
        s2.close();
        s3.close();
    }

    private void sendMessage(Session s, Message m) throws IOException, EncodeException, InterruptedException {
        /* Wait until the queue is empty to send this message (and *produce* another response) */
        if (!requestMade || expectedQueue.size() == 0)
        {
            requestMade = true;
        }
        else
        {
            /* Ensure we got the response we expected before we proceed to the next request. */
            Message readMessage = msgQueue.take();
            Message expectedMessage = expectedQueue.poll();
            if (!expectedMessage.equals(readMessage))
            {
                assertFalse(true);
            }
        }
        s.getBasicRemote().sendObject(m);
    }
}
