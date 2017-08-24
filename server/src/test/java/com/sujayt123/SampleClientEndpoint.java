package com.sujayt123;

import com.sujayt123.communication.MessageDecoder;
import com.sujayt123.communication.MessageEncoder;
import com.sujayt123.communication.msg.Message;

import javax.websocket.*;

/**
 * Created by sujay on 8/23/17.
 */
@ClientEndpoint(decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class SampleClientEndpoint {
    @OnMessage
    public void onMessage(Session s, Message m)
    {
        ScrabbleEndpointIntegrationTest.msgQueue.add(m);
    }
}
