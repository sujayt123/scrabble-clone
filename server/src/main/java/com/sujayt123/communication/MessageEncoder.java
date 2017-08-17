package com.sujayt123.communication;

import com.sujayt123.communication.msg.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * An encoder from Messages into stringified JSON to send over network.
 */
public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public String encode(Message message) throws EncodeException {
        return message.toJson();
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}