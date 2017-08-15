package communication.msg;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import communication.msg.server.StringMessage;

/**
 * A generic message for communication between the client and server.
 *
 * To enable reliable communication, we must enforce a strict contract between the nature of
 * the message (as indicated by the MessageType) and the data it contains.
 *
 * Contract:
 * 1. Every concrete message need only supply the fields it wants to contain.
 *    - The fields must be simple enough that gson.fromJson(s, getClass()) works.
 *
 * Created by sujay on 8/14/17.
 */
public abstract class Message {

    private String type;

    /* */
    public Message()
    {
        this.type = getClass().toString();
    }

    /**
     * Every message must implement a toJson method that returns
     * a stringified representation of its converted-to-json contents.
     *
     * @return a stringified representation of the message in json
     */
    public String toJson(){
        // Assuming the connection (and therefore the serialized JSON) is encrypted, we can keep the user data as plain-text.
        Gson gson = new Gson();
        return gson.toJson(this, getClass());
    }
}
