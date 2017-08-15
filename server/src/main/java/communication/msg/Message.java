package communication.msg;

/**
 * A generic message for communication between the client and server.
 *
 * To enable reliable communication, we must enforce a strict contract between the nature of
 * the message (as indicated by the MessageType) and the data it contains.
 *
 * Contract:
 * 1. Every concrete message must include a no-args constructor (or have no constructor at all).
 * 2. Every concrete message must implement the toJson and consumeJson methods.
 *
 * Created by sujay on 8/14/17.
 */
public abstract class Message {

    /**
     * Every message must implement a toJson method that returns
     * a stringified representation of its converted-to-json contents.
     *
     * Each message MUST include "type" -> class name
     * as one of the k-v pairs.
     *
     * @return a stringified representation of the message in json
     */
    public abstract String toJson();

    /**
     * Every message must be able to update its properties with
     * a provided json representation of the properties to set.
     */
    public abstract void consumeJson(String s);
}
