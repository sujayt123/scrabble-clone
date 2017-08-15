package communication;

import communication.msg.Message;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.util.Optional;

/**
 * A decoder from raw stringified JSON into the appropriate Message.
 */
public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(String s) throws DecodeException {
        Optional<Message> msg = MessageBuilder.build(s);
        if (!msg.isPresent())
        {
            throw new DecodeException(s, "Could not parse provided string into a message.");
        }
        return msg.get();
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}