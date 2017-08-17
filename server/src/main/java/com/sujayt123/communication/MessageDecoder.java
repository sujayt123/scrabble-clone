package com.sujayt123.communication;

import com.sujayt123.ScrabbleEndpoint;
import com.sujayt123.communication.msg.Message;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.util.Optional;
import java.util.logging.Level;

/**
 * A decoder from raw stringified JSON into the appropriate Message.
 */
public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(String s) throws DecodeException {
        ScrabbleEndpoint.logr.log(Level.INFO, "The string I'm attempting to decode is: " + s);
        Optional<Message> msg = MessageBuilder.build(s);
        if (!msg.isPresent())
        {
            throw new DecodeException(s, "Could not parse provided string into a message.");
        }
        return msg.get();
    }

    @Override
    public boolean willDecode(String s) {
        return s != null && s.length() > 0;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}