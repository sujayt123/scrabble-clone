package com.sujayt123.communication;

import com.sujayt123.communication.msg.Message;

import com.google.gson.Gson;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Optional;

/**
 * Builds the appropriate Message instance from a provided stringified-JSON.
 *
 * Created by sujay on 8/14/17.
 */
public class MessageBuilder {

    public static Optional<Message> build(String s) {
        // Parse the stringified-json into a temporary javax.json object...
        JsonObject jsonObject = Json
                .createReader(new StringReader(s)).readObject();
        // ... so that you can determine the class type.
        String classType = jsonObject.getString("type");

        /*
         * Once you know the class type, use Gson in conjunction
         * with java reflection to build the object in question.
         */

        Class<?> myClass;

        try {
            myClass = Class.forName(classType);
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }

        Gson gson = new Gson();
        return Optional.of((Message)gson.fromJson(s, myClass));
    }

}
