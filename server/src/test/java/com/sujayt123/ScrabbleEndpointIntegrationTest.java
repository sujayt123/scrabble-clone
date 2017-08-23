package com.sujayt123;

import com.sujayt123.communication.MessageDecoder;
import com.sujayt123.communication.MessageEncoder;
import com.sujayt123.communication.msg.Message;
import org.glassfish.tyrus.client.ClientManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.*;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by sujay on 8/23/17.
 */
public class ScrabbleEndpointIntegrationTest {
    static Queue<Message> msgQueue;

    @Before
    public void setUp() throws Exception
    {
        /* Instantiate a new message queue for the test */
        msgQueue = new ConcurrentLinkedDeque<>();
    }

    @Test
    public void testEndpoint() throws URISyntaxException, InterruptedException, DeploymentException, IOException {
        org.glassfish.tyrus.server.Server server =
                new org.glassfish.tyrus.server.Server("localhost", 8080, null, null, com.sujayt123.ScrabbleEndpoint.class);

        try {
            System.out.println("Starting testing server ...");
            server.start();

            System.out.println("Creating new headless client...");
            ClientManager client = ClientManager.createClient();

            System.out.println("Connecting to server... ");
            Session session = client.connectToServer(DemoClientEndpoint.class, new URI("ws://localhost:8080/connect"));

            /* Send the requests you want; their responses will be added in order in the deque. */
//            session.getBasicRemote().sendObject();

            /*  Wait 5 seconds for the server to process all requests (slightly hacky, I know) */
            Thread.sleep(5000);

            /* Verify all requests were accurate. */

        } finally {
            server.stop();
        }
    }


    @ClientEndpoint(encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
    private static class DemoClientEndpoint
    {
        private final String serverUri="ws://localhost:8080/connect";

        @OnOpen
        public void onOpen(Session session)
        {
            System.out.println("Opened a new websocket client.");
        }

        @OnMessage
        public void onMessage(Message message, Session session)
        {
            ScrabbleEndpointIntegrationTest.msgQueue.add(message);
        }

        @OnClose()
        public void onClose(Session session)
        {
            System.out.println("Closed a websocket client.");
        }

    }



}